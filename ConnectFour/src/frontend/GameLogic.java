package frontend;

public class GameLogic implements ViewController, MiddleWare {
	
	protected static final int TURN_TIME = 45; // time limit per turn in seconds
	
	protected static final String WINNING_STRING = "YOU WIN";
	protected static final String DRAW_STRING = "DRAW";
	protected static final String LOSING_STRING = "YOU LOSE";
	
	private int timeLeft;
	
	private boolean userTurn;
	private int gameWinner; // 0 = nobody
	private int userColor, opponentColor; // 1 or 2
	
	private GameCanvas gc;
	private WindowFrame f;
	private int[][] positions;
	
	public GameLogic(WindowFrame f, boolean userTurn, int userColor) {
		this.userTurn = userTurn;
		this.gameWinner = 0;
		this.userColor = userColor;
		this.opponentColor = (userColor % 2) + 1; // 1 -> 2; 2 -> 1
		this.positions = new int[7][6];
		this.gc = new GameCanvas(this);
		this.f = f;
		MessageHandler.listen(this);
		resetTimer();
	}
	
	private void resetTimer() {
		this.timeLeft = TURN_TIME * GameCanvas.FPS;
	}
	
	// returns time left
	public int updateTimer() {
		if (--this.timeLeft == 0) {
			// TODO Synchronize to ensure both players agree
			this.gameWinner = userTurn ? opponentColor : userColor;
		}
		
		return this.timeLeft;
	}
	
	public void placeToken(int column) {
		MessageHandler.sendMessage(this, column);
		this.dropToken(column, this.userColor);
	}
	
	// Called when signal of other player's play is sent
	public void receiveToken(int column) {
		this.dropToken(column, this.opponentColor);
	}
	
	public void dropToken(int column, int type) {
		
		if (this.positions[column][0] != 0)
			return; // error
		
		if (type != 1 && type != 2)
			return; // error
		
		int row = 5;
		
		for (int j = 1; j < 6; j++) {
			if (this.positions[column][j] != 0) {
				row = j - 1;
				break;
			}
		}
		
		this.gc.drop(column, row, type);
	}
	
	public void insertToken(int column, int row, int type) {
		this.positions[column][row] = type;
		
		resetTimer();
		computeWinner();
		this.userTurn = !this.userTurn;
	}
	
	public void computeWinner() {
		// Uncomment loop to print board
		//printBoard();
		if (checkDiagonalWinner(0, 5, 1, -1) > 0) return;
		if (checkDiagonalWinner(6, 0, -1, 1) > 0) return;
		if (checkDiagonalWinner(6, 5, -1, -1) > 0) return;
		if (checkDiagonalWinner(0, 0, 1, 1) > 0) return;
		if (checkStraightWinner() > 0) return;
		if (boardIsFull()) return;
		this.gameWinner = 0;
	}
	
	@SuppressWarnings("unused")
	private void printBoard() {
		for (int j = 0; j < 6; j++) {
			 for (int i = 0; i < 7; i++) {
				 System.out.print(this.positions[i][j]);
			 }
			 System.out.println();
		}
		System.out.println();
	}
	
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
	
	private boolean boardIsFull() {
		for (int i = 0; i < 7; i++) {
			if (this.positions[i][0] == 0)
				return false;
		}
		this.gameWinner = 3;
		return true;
	}

	public int getWinner() {
		return this.gameWinner;
	}

	public int getTimeLeft() {
		return timeLeft;
	}
	
	public boolean isWinner() {
		return this.gameWinner == this.userColor;
	}
	
	public boolean isDraw() {
		return this.gameWinner == 3;
	}
	
	public int[][] getPositions() {
		return this.positions;
	}

	public boolean isUserTurn() {
		return this.userTurn;
	}
	
	public int getUserColor() {
		return this.userColor;
	}
	
	public int getOpponentColor() {
		return this.opponentColor;
	}
	
	@Override
	public Canvas getCanvas() {
		return this.gc;
	}

	public void exit() {
		MessageHandler.sendMessage(this, MessageHandler.GAME_OVER);
		this.f.insult(isWinner());
	}

	@Override
	public void transferData(int data) {
		System.out.println(data);
		if (data == MessageHandler.GAME_OVER) {
			exit();
			return;
		}
		
		if (data == MessageHandler.DISCONNECT_SIGNAL) {
			this.f.displayError(ErrorLogic.DISONNECT_MESSAGE);
			return;
		}
		
		if (data >= 0 && data < 7)
			receiveToken(data);
	}
	
	@Override
	public void transferFail() {
		this.f.displayError(ErrorLogic.TRANSFER_FAIL);
	}
	
	@Override
	public ViewID getID() {
		return ViewID.GAME;
	}
	
	@Override
	public void cleanUp() {
		MessageHandler.closeMessageListener();
		this.gc.cleanUp();
	}

	@Override
	public void disconnect() {
		MessageHandler.sendDisconnect(this);
	}
}
