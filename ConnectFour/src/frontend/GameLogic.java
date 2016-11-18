package frontend;

/**
 * Game controller: Responsible for the gameplay logic.
 */
public class GameLogic implements ViewController, MiddleWare {
	
	protected static final int TURN_TIME = 45; // time limit per turn in seconds
	protected static final int LEEWAY_TIME = 5; // grace time for the loser to acknowledge their loss in seconds
	
	protected static final String WINNING_STRING = "YOU WIN";
	protected static final String DRAW_STRING = "DRAW";
	protected static final String LOSING_STRING = "YOU LOSE";
	
	private int timeLeft;
	private int leeway;
	
	private boolean userTurn;
	private int gameWinner; // 0 = nobody
	private int userColor, opponentColor; // 1 or 2
	
	private GameCanvas gc;
	private WindowFrame f;
	private int[][] positions;
	
	private MessageHandler mh;
	
	/**
	 * Constructor: launches the network configuration, sets up the initial game data, and launches the view.
	 * @param f Pointer to the frame containing the view.
	 * @param userTurn Whether the player must make the starting move.
	 * @param userColor Color of the player's tokens represented as an integer (1 or 2).
	 */
	public GameLogic(WindowFrame f, boolean userTurn, int userColor) {
		this.userTurn = userTurn;
		this.gameWinner = 0;
		this.userColor = userColor;
		this.opponentColor = (userColor % 2) + 1; // 1 -> 2; 2 -> 1
		this.positions = new int[7][6];
		this.f = f;
		this.mh = NetworkConfiguration.getMessageHandler();
		this.mh.listen(this);
		resetTimer();
		this.gc = new GameCanvas(this);
	}
	
	/**
	 * Reset the time left, this is done every time a turn is completed.
	 */
	private void resetTimer() {
		this.timeLeft = TURN_TIME * GameCanvas.FPS;
		this.leeway = LEEWAY_TIME * GameCanvas.FPS;
	}
	
	/**
	 * Every time the view ticks, the timer is updated. If the time runs out and it's the player's turn, the player notifies their opponent.
	 * If the time runs out and it's the player's opponent's turn, the player waits a few more seconds, and if nothing happens, this is treated
	 * as an opponent disconnect.
	 * @return Time left in number of frames.
	 */
	public int updateTimer() {
		if (this.timeLeft == 0) {
			if (this.userTurn) {
				this.mh.sendMessage(this, MessageHandler.TIME_OUT_SYNC);
				this.gameWinner = opponentColor;
			} else {
				if (this.leeway == 0) {
					this.f.displayError(ErrorLogic.DISONNECT_MESSAGE);
				} else {
					this.leeway--;
				}
			}
		} else {
			this.timeLeft--;
		}
		return this.timeLeft;
	}
	
	/**
	 * Method called when the player places a token. Send the info to the opponent and launch the drop token animation.
	 * @param column Column number (range: 0-6 incl.)
	 */
	public void placeToken(int column) {
		this.mh.sendMessage(this, getCheckNumberHash());
		this.mh.sendMessage(this, column);
		this.dropToken(column, this.userColor);
	}
	
	/**
	 * Method called when the opponent places a token, i.e. when the transmission is received. Launch the drop token animation.
	 * @param column Column number (range: 0-6 incl.)
	 */
	public void receiveToken(int column) {
		this.dropToken(column, this.opponentColor);
	}
	
	/**
	 * Launch the drop token animation in the view.
	 * @param column Column where the token will be dropped.
	 * @param type Color of the token as integer (1 or 2).
	 */
	public void dropToken(int column, int type) {
		
		if (this.positions[column][0] != 0)
			throw new RuntimeException("Token being placed in full column.");
		
		if (type != 1 && type != 2)
			throw new RuntimeException("Invalid token type.");
		
		int row = 5;
		
		for (int j = 1; j < 6; j++) {
			if (this.positions[column][j] != 0) {
				row = j - 1;
				break;
			}
		}
		
		this.gc.drop(column, row, type);
	}
	
	/**
	 * Once the drop token animation is over, place the token permanently in the grid.
	 * @param column Column where the token will reside.
	 * @param row Row where the token will reside.
	 * @param type Token color.
	 */
	public void insertToken(int column, int row, int type) {
		this.positions[column][row] = type;
		
		resetTimer();
		computeWinner();
		this.userTurn = !this.userTurn;
	}
	
	/**
	 * Update the gameWinner variable (0 is nobody and 3 is a draw). Computed every update.
	 */
	public void computeWinner() {
		if (checkDiagonalWinner(0, 5, 1, -1) > 0) return;
		if (checkDiagonalWinner(6, 0, -1, 1) > 0) return;
		if (checkDiagonalWinner(6, 5, -1, -1) > 0) return;
		if (checkDiagonalWinner(0, 0, 1, 1) > 0) return;
		if (checkStraightWinner() > 0) return;
		if (boardIsFull()) return;
		this.gameWinner = 0;
	}
	
	/**
	 * Helper function to check if a vertical connect 4 is achieved.
	 * @return Color of user who achieved it (0 if no one).
	 */
	private int checkStraightWinner() {
		for (int i = 0; i < 7; i++) {
			int previousVertical = -1;
			int sameVertical = 1;
			for (int j = 0; j < 6; j++) {
				int current = this.positions[i][j];
				if (current == 0) {
					previousVertical = -1;
					continue;
				}
				// check horizontally
				if (i > 2 	&& current == this.positions[i - 1][j]
						 	&& current == this.positions[i - 2][j]
						 	&& current == this.positions[i - 3][j]) {
					this.gameWinner = current;
					return current;
				}
				if (current == previousVertical) {
					sameVertical++;
				}
				else {
					sameVertical = 1;
				}
				if (sameVertical == 4) {
					this.gameWinner = current;
					return current;
				}
				previousVertical = current;
			}
		}
		return 0;
	}
	
	/**
	 * Helper function to check if a vertical connect 4 is achieved.
	 * @param startRow Row where the diagonal begins.
	 * @param startColumn Column where the diagonal begins.
	 * @param rowIncrement Whether the diagonal points left or right.
	 * @param colIncrement Whether the diagonal points up or down.
	 * @return Color of user who achieved it (0 if no one).
	 */
	private int checkDiagonalWinner(int startRow, int startColumn, int rowIncrement, int colIncrement) {
		for (int i = startRow; (i < 7 && i > -1); i+= rowIncrement) {
			int currRow = i;
			int count = 1;
			int previous = -1;
			for (int j = startColumn; (j < 6 && j > -1) && (currRow > -1 && currRow < 7); j+= colIncrement) {
				int current = this.positions[currRow][j];
				if (current == 0) {
					previous = -1;
					continue;
				}
				if (current == previous)
					count++;
				else
					count = 1;
				if (count == 4) {
					this.gameWinner = current;
					return current;
				}
				previous = current;
				currRow += rowIncrement;
			}
		}
		return 0;
	}
	
	/**
	 * Check whether the grid is completely populated with tokens. If there are no winners and that is a case, there is a draw.
	 * @return True if the grid if full, false otherwise.
	 */
	private boolean boardIsFull() {
		for (int i = 0; i < 7; i++) {
			if (this.positions[i][0] == 0)
				return false;
		}
		this.gameWinner = 3;
		return true;
	}
	
	/**
	 * Generates a number unique to the current state of the board.
	 * @return Board value.
	 */
	private int getCheckNumber() {
		// TODO Generate a unique number per board; ideally as small as possible; and with as much information as possible
		// in the least significant bits.
		// Currently is a placeholder that just sums the values of the token, not unique at all.
		int sum = 0;
		for (int i = 0; i < 7; i++)
			for (int j = 0; j < 6; j++)
				sum += this.positions[i][j];
		
		return sum;
	}
	
	/**
	 * Returns the value of the board constrained within a byte and above all other signals.
	 * Called in one of two possible scenarios:
	 * 1. Player makes a move, call it before move is made and transmit it to the opponent.
	 * 2. Player receives analogous value from opponent, call it to compare values and ensure data is not corrupted.
	 * @return Hashed check value.
	 */
	private int getCheckNumberHash() {
		return MessageHandler.CHECK_NUMBER_LOW + (getCheckNumber() % (256 - MessageHandler.CHECK_NUMBER_LOW));
	}

	/**
	 * Getter function for the winner's color.
	 * @return Winner's color; special cases: 0 if nobody, 3 if draw.
	 */
	public int getWinner() {
		return this.gameWinner;
	}

	/**
	 * Getter function for time left during this turn.
	 * @return Time left in number of frames.
	 */
	public int getTimeLeft() {
		return timeLeft;
	}
	
	/**
	 * Returns whether current player is the winner.
	 * @return True if the player is the winner, false otherwise.
	 */
	public boolean isWinner() {
		return this.gameWinner == this.userColor;
	}
	
	/**
	 * Returns whether the game is a draw (or tie).
	 * @return True if the game is a draw, false otherwise.
	 */
	public boolean isDraw() {
		return this.gameWinner == 3;
	}
	
	/**
	 * Getter function for the grid represented as a 2D array.
	 * @return 2D array representation of the grid.
	 */
	public int[][] getPositions() {
		return this.positions;
	}

	/**
	 * Returns whether it is currently the player's turn.
	 * @return True if it is the player's turn, false otherwise.
	 */
	public boolean isUserTurn() {
		return this.userTurn;
	}
	
	/**
	 * Getter function for the player's color.
	 * @return Player's color as integer (1 or 2).
	 */
	public int getUserColor() {
		return this.userColor;
	}
	
	/**
	 * Getter function for the player's opponent's color.
	 * @return Opponent's color as integer (1 or 2).
	 */
	public int getOpponentColor() {
		return this.opponentColor;
	}
	
	@Override
	public Canvas getCanvas() {
		return this.gc;
	}

	/**
	 * Function called when the player wants to move on to the insult view. If it's a draw, queue up.
	 */
	public void exit() {
		this.mh.sendMessage(this, MessageHandler.GAME_OVER);
		this.gc.cleanUp();
		this.f.insult(isWinner());
	}

	@Override
	public void transferData(int data) {
		if (data == MessageHandler.GAME_OVER) {
			exit();
			return;
		}
		
		if (data == MessageHandler.DISCONNECT_SIGNAL) {
			this.f.displayError(ErrorLogic.DISONNECT_MESSAGE);
			return;
		}
		
		if (data == MessageHandler.TIME_OUT_SYNC) {
			this.gameWinner = userColor;
			return;
		}
		
		if (data >= MessageHandler.CHECK_NUMBER_LOW && data != getCheckNumberHash()) {
			this.f.displayCriticalError(ErrorLogic.CHECK_FAIL);
			return;
		}
				
		if (data >= 0 && data < 7) {
			receiveToken(data);
			return;
		}
	}
	
	@Override
	public void transferFail() {
		this.f.displayCriticalError(ErrorLogic.TRANSFER_FAIL);
	}
	
	@Override
	public ViewID getID() {
		return ViewID.GAME;
	}
	
	@Override
	public void cleanUp() {
		this.mh.close();
		this.gc.cleanUp();
	}

	@Override
	public void disconnect() {
		this.mh.sendMessage(this, MessageHandler.DISCONNECT_SIGNAL);
		try {
			// Give some time for the signal to go out.
			Thread.sleep(MessageHandler.GRACE_PERIOD * 1000);
		} catch (InterruptedException e) {
			// If the signal still didn't go out, the other player will have to wait for their timer to run out, and they will know then.
			// No big deal.
		}
	}
}
