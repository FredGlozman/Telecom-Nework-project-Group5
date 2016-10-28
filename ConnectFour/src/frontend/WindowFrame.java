package frontend;

import javax.swing.JFrame;

public class WindowFrame extends JFrame {
	protected static final int CASE = 1;
	
	protected static final String IP = "localhost";
	protected static final int PORT_1 = 8877 + CASE;
	protected static final int PORT_2 = 8878 - CASE;
	protected static final int USER_NUMBER = 1 + CASE;
	protected static final boolean START = CASE == 0;
	
	
	private static final long serialVersionUID = -2938584211301935192L;

	protected static final int DEFAULT_WIDTH_HEIGHT = 1500; // do not change
	
	protected static final int WIDTH = 700; // edit this to change window size;
	protected static final int HEIGHT = WIDTH; // edit at your own risk, usually works best as a square

	
	public static final String TITLE = "Connect Four";
	
	public WindowFrame() {
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		waitForPlayers();
	}
	
	public void startGame() {
		setVisible(false);
		setContentPane(new GameLogic(this, START, USER_NUMBER).getCanvas());
		setVisible(true);
	}
	
	public void waitForPlayers() {
		setVisible(false);
		setContentPane(new WaitLogic(this).getCanvas());
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new WindowFrame();
	}

}
