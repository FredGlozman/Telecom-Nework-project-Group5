package frontend;

import java.util.Arrays;

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
//		// Uncomment to print the board
//		for (int i = 0; i < 7; i++)
//			System.out.println(i + ": " + Arrays.toString(this.positions[i]));
//		System.out.println();
		if (checkDiagonalWinner(0, 5, 1, -1) > 0) return;
		if (checkDiagonalWinner(6, 0, -1, 1) > 0) return;
		if (checkDiagonalWinner(6, 5, -1, -1) > 0) return;
		if (checkDiagonalWinner(0, 0, 1, 1) > 0) return;
		if (checkStraightWinner() > 0) return;
		this.gameWinner = 0;
	}
	
	private int checkStraightWinner() {
		int previousVertical = -1;
		for (int i = 0; i < 7; i++) {
			int sameVertical = 1;
			for (int j = 5; j > -1; j--) {
				int current = this.positions[i][j];
				if (current == 0)
					continue;
				// check horizontally
				if (i > 2 	&& current == this.positions[i - 1][j]
						 	&& current == this.positions[i - 2][j]
						 	&& current == this.positions[i - 3][j]) {
					System.out.println("horizontal win: " + current);
					this.gameWinner = current;
					return current;
				}
				if (current == previousVertical)
					sameVertical++;
				else {
					sameVertical = 1;
				}
				if (sameVertical == 4) {
					System.out.println("vertical win: " + current);
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
				//System.out.println("(" + currRow +" , " + j +"): " + current);
				if (current == previous) {
					count++;
				}
				else
					count = 1;
				if (count == 4) {
					//System.out.println("start row: " + startRow + " start col: " + startColumn );
					
					System.out.println("diagonal win: " + current);
					this.gameWinner = current;
					return current;
				}
				previous = current;
				currRow += rowIncrement;
			}
		}
		return 0;
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
