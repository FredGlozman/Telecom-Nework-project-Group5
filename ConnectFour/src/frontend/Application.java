package frontend;

/**
 * Wrapper for the program's entry point. 
 */
public class Application {
	
	/**
	 * Entry point of the program: starts are the matchmaking queue.
	 * @param args Default Java command-line arguments.
	 */
	public static void main(String[] args) {
		// Initialize view by creating a window and launch the wait.
		new WindowFrame().waitForPlayers();
	}
}