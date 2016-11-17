package frontend;

/**
 * Error controller: Responsible for the error message view and subsequent actions.
 */
public class ErrorLogic implements ViewController {
	
	protected static final String DISONNECT_MESSAGE = "Opponent has disconnected.";
	protected static final String TRANSFER_FAIL = "Could not send packet.";

	private ErrorCanvas ec;
	private WindowFrame f;
	private String errorMessage;
	private ErrorSeverity severity;
	
	/**
	 * Constructor, sets up the error information, and launches the view.
	 * @param f Pointer to the frame containing the view.
	 * @param errorMessage Error message as a string.
	 * @param severity Severity of the error: detemines the follow-up action upon dismissal.
	 */
	public ErrorLogic(WindowFrame f, String errorMessage, ErrorSeverity severity) {
		this.severity = severity;
		this.errorMessage = errorMessage;
		this.f = f;
		this.ec = new ErrorCanvas(this);
	}
	
	/**
	 * Constructor without severity, default severity being normal.
	 * @param f Pointer to the frame containing the view.
	 * @param errorMessage Error message as a string.
	 */
	public ErrorLogic(WindowFrame f, String errorMessage) {
		this.severity = ErrorSeverity.NORMAL;
		this.errorMessage = errorMessage;
		this.f = f;
		this.ec = new ErrorCanvas(this);
	}
	
	/**
	 * Error dismissal triggered upon mouse click in the view.
	 */
	public void dismissError() {
		switch (this.severity) {
			// If the severity is normal, the next step it to get back into the matchmaking queue
			case NORMAL:
				this.f.waitForPlayers();
				break;
			// If the severity is critical, close the game upon dismissal.
			case CRITICAL:
				this.f.exit();
				break;
			default:
				break;
		}
	}
	
	/**
	 * Error header, indicating to the user the severity of the error via the view.
	 * @return String to draw, e.g. "Error" if the severiy is normal.
	 */
	public String getErrorHeader() {
		switch (this.severity) {
			case NORMAL:
				return "Error";
			case CRITICAL:
				return "Critical Error";
			default:
				return null;
		}
	}
	
	/**
	 * Getter for the error message.
	 * @return Error message string.
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	@Override
	public Canvas getCanvas() {
		return this.ec;
	}

	@Override
	public ViewID getID() {
		return ViewID.ERROR;
	}

	@Override
	public void cleanUp() {
		this.ec.cleanUp();
	}

	@Override
	public void disconnect() {}

}
