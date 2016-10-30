package frontend;

public class ErrorLogic implements ViewController {
	
	protected static final String DISONNECT_MESSAGE = "Opponent has disconnected";
	protected static final String TRANSFER_FAIL = "Could not send packet";

	private ErrorCanvas ec;
	private WindowFrame f;
	private String errorMessage;
	private ErrorSeverity severity;
	
	public ErrorLogic(WindowFrame f, String errorMessage) {
		this.severity = ErrorSeverity.NORMAL;
		this.errorMessage = errorMessage;
		this.f = f;
		this.ec = new ErrorCanvas(this);
	}
	
	public ErrorLogic(WindowFrame f, String errorMessage, ErrorSeverity severity) {
		this.severity = severity;
		this.errorMessage = errorMessage;
		this.f = f;
		this.ec = new ErrorCanvas(this);
	}
	
	public void dismissError() {
		switch (this.severity) {
			case NORMAL:
				this.f.waitForPlayers();
				break;
			case CRITICAL:
				this.f.exit();
				break;
			default:
				break;
		}
	}
	
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

}
