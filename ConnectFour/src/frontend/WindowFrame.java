package frontend;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import backend.Player;

/**
 * Outermost frame; this is the window itself. It contains and can switch to any view.
 */
public class WindowFrame extends JFrame {
	private static final long serialVersionUID = -2938584211301935192L;

	protected static final int DEFAULT_WIDTH_HEIGHT = 1500; // do not change
	
	protected static final int WIDTH = 700; // edit this to change window size;
	protected static final int HEIGHT = WIDTH; // edit at your own risk, usually works best as a square

	public static final String TITLE = "Connect Four";
	
	private ViewController vc;
	
	/**
	 * Constructor: creates the window and pops it up smack in the middle.
	 */
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
	
	/**
	 * Launch a game.
	 * @param me Player's information.
	 * @param opponent Opponent's information.
	 */
	public void startGame(Player me, Player opponent) {
		int coin = me.getCoin();
		switchView(new GameLogic(this, coin == 0, coin + 1));
	}
	
	/**
	 * Get in queue.
	 */
	public void waitForPlayers() {
		if (vc != null)
			vc.cleanUp();
		switchView(new WaitLogic(this));
	}
	
	/**
	 * Show an error message.
	 * @param errorMessage Error message to show (string).
	 */
	public void displayError(String errorMessage) {
		displayError(errorMessage, ErrorSeverity.NORMAL);
	}
	
	/**
	 * Show a critical error message.
	 * @param errorMessage Error message to show (string).
	 */
	public void displayCriticalError(String errorMessage) {
		displayError(errorMessage, ErrorSeverity.CRITICAL);
	}
	
	/**
	 * Show an error message of a specified severity.
	 * @param errorMessage Error message to show (string).
	 * @param errorSeverity Severity of the error (e.g. CRITICAL).
	 */
	private void displayError(String errorMessage, ErrorSeverity errorSeverity) {
		if (vc.getID() != ViewID.ERROR) {
			vc.cleanUp();
			switchView(new ErrorLogic(this, errorMessage, errorSeverity));
		}
	}
	
	/**
	 * Launch the insult view.
	 * @param winner Whether the player is the winner.
	 */
	public void insult(boolean winner) {
		switchView(new InsultLogic(this, winner));
	}
	
	/**
	 * Switch between views. Every time a different phase of the game begins, the view must be switched.
	 * This changes the contents of the window and resets its visibility to amplify focus request.
	 * @param vc Controller of the new view.
	 */
	private void switchView(ViewController vc) {
		this.vc = vc;
		setContentPane(vc.getCanvas());
		setVisible(false);
		setVisible(true);
	}
	
	/**
	 * Called when the window closes. Usually, this transmits a disconnect signal, cleans up the current view,
	 * and exits the whole program.
	 */
	public void exit() {
		vc.disconnect();
		vc.cleanUp();
		System.exit(0);
	}

}
