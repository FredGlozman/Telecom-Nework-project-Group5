package frontend;

import javax.swing.JFrame;

public class WindowFrame extends JFrame {
	
	private static final long serialVersionUID = -2938584211301935192L;

	protected static final int WIDTH = 1500;
	protected static final int HEIGHT = 1500;
	public static final String TITLE = "Connect Four";
	
	public WindowFrame() {
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle(TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// TODO Flow of game
//		waitForPlayers();
		startGame();
	}
	
	public void startGame() {
		setVisible(false);
		setContentPane(new GameLogic(this, true, 1).getCanvas());
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
