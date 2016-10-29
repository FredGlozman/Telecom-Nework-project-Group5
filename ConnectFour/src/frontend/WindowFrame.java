package frontend;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import backend.MessageTransmitter;
import backend.ServerTextFileIO;

public class WindowFrame extends JFrame {
	protected static final int QUIT_COLUMN = 7;
	
	private static final long serialVersionUID = -2938584211301935192L;

	protected static final int DEFAULT_WIDTH_HEIGHT = 1500; // do not change
	
	protected static final int WIDTH = 700; // edit this to change window size;
	protected static final int HEIGHT = WIDTH; // edit at your own risk, usually works best as a square

	public static final String TITLE = "Connect Four";
	
	private ViewID vID;
	
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
	
	public void startGame() {
		switchView(new GameLogic(this, NetworkConfiguration.START, NetworkConfiguration.USER_NUMBER));
	}
	
	public void waitForPlayers() {
		switchView(new WaitLogic(this));
	}
	
	public void displayError(String errorMessage) {
		switchView(new ErrorLogic(this, errorMessage));
	}
	
	public void displayCriticalError(String errorMessage) {
		switchView(new ErrorLogic(this, errorMessage, ErrorSeverity.CRITICAL));
	}
	
	private void switchView(ViewController v) {
		setVisible(false);
		this.vID = v.getID();
		setContentPane(v.getCanvas());
		setVisible(true);
	}
	
	public void exit() {
		switch (vID) {
			case GAME:
				(new MessageTransmitter(NetworkConfiguration.IP, QUIT_COLUMN, NetworkConfiguration.PORT_1)).start();
				break;
			case WAIT:
				ServerTextFileIO.getInstance().clear();
				break;
			case ERROR:
				break;
			default:
				break;
		}
		
		System.exit(0);
	}

}
