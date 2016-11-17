package frontend;

/**
 * Structure of all controllers.
 */
public interface ViewController {
	/**
	 * Clean up every related aspect to the controller. This usually implies potentially breaking a connection
	 * and cleaning up the UI (i.e. stopping timers and losing focus).
	 */
	public void cleanUp();
	
	/**
	 * Called when the user closes the window while in the controller's view. Usually meant to inform the opponent
	 * that the user has disconnected. If there is no opponent, this may do nothing at all.
	 */
	public void disconnect();
	
	/**
	 * Getter method for the view associated to the controller.
	 * @return View associated to the controller.
	 */
	public Canvas getCanvas();
	
	/**
	 * Getter method for the ID of the controller's view. Each class of controller has a specific ID associated to it
	 * (e.g. GAME, INSULT). They are all listed in the ViewID enumeration.
	 * @return View's ID.
	 */
	public ViewID getID(); 
}