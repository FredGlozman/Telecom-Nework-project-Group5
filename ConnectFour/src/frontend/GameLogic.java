package frontend;

public class GameLogic implements ViewController {
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
	}
	
	public void placeToken(int column) {
		// TODO Send play to other player
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
		computeWinner();
		// TODO Comment in one line below, delete two lines below 
		// this.userTurn = !this.userTurn;
		this.userColor = this.userColor % 2 + 1;
	}
	
	public void computeWinner() {
		// TODO Compute winner and set this.gameWinner to it: 0 = nobody, 1 = type 1, 2 = type 2
		this.gameWinner = 0;
	}
	
	public int getWinner() {
		return this.gameWinner;
	}
	
	public boolean isWinner() {
		return this.gameWinner == this.userColor;
	}
	
	public int[][] getPositions() {
		return this.positions;
	}

	public boolean isUserTurn() {
		return this.userTurn;
	}
	
	@Override
	public Canvas getCanvas() {
		return this.gc;
	}

	public void rematch() {
		this.f.waitForPlayers();
	}
	
}
