package frontend;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import backend.Player;

public class WindowFrame extends JFrame {
	private static final long serialVersionUID = -2938584211301935192L;

	protected static final int DEFAULT_WIDTH_HEIGHT = 1500; // do not change
	
	protected static final int WIDTH = 700; // edit this to change window size;
	protected static final int HEIGHT = WIDTH; // edit at your own risk, usually works best as a square

	public static final String TITLE = "Connect Four";
	
	private ViewController vc;
	
	public WindowFrame() {
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle(TITLE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
	}
	
	public void startGame(Player me, Player opponent) {
		int coin = me.getCoin();
		switchView(new GameLogic(this, coin == 0, coin + 1));
	}
	
	public void waitForPlayers() {
		if (vc != null)
			vc.cleanUp();
		switchView(new WaitLogic(this));
	}
	
	public void displayError(String errorMessage) {
		displayError(errorMessage, ErrorSeverity.NORMAL);
	}
	
	public void displayCriticalError(String errorMessage) {
		displayError(errorMessage, ErrorSeverity.CRITICAL);
	}
	
	private void displayError(String errorMessage, ErrorSeverity errorSeverity) {
		if (vc.getID() != ViewID.ERROR) {
			vc.cleanUp();
			switchView(new ErrorLogic(this, errorMessage, errorSeverity));
		}
	}
	
	public void insult(boolean winner) {
		switchView(new InsultLogic(this, winner));
	}
	
	private void switchView(ViewController vc) {
		this.vc = vc;
		setContentPane(vc.getCanvas());
		setVisible(false);
		setVisible(true);
	}
	
	public void exit() {
		vc.disconnect();
		vc.cleanUp();
		System.exit(0);
	}

}
