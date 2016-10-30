package frontend;

public class InsultLogic implements ViewController, MiddleWare {

	protected static final int MAX_INSULT_LENGTH = 32; // Maximum number of characters the insult can contain
	protected static final int INSULT_TIME_LIMIT = 18; // Number of seconds the user has to type up their insult
	protected static final int INSULT_DISPLAY_TIME = 7; // Number of seconds the loser is forced to see the insult for
	protected static final int LOSER_EXTRA_TIME = 2; // Number of additional seconds for the loser
	
	private InsultCanvas ic;
	private WindowFrame f;
	private boolean winner;
	private String insult;
	private int timeLeft, timeSet;
	private int phase;
	private int sendingIndex;
	
	public InsultLogic(WindowFrame f, boolean winner) {
		this.f = f;
		this.winner = winner;
		this.insult = new String();
		this.phase = 0;
		this.sendingIndex = 0;
		this.setTimer(INSULT_TIME_LIMIT);
		this.ic = new InsultCanvas(this);
		MessageHandler.listen(this);
	}
	
	private void setTimer(int seconds) {
		this.phase++;
		this.timeLeft = this.timeSet = (seconds + (winner ? 0 : LOSER_EXTRA_TIME)) * GameCanvas.FPS;
	}
	
	// returns time left
	public int updateTimer() {
		if (--this.timeLeft == 0) {
			exit();
		}
		
		return this.timeLeft;
	}
	
	public int getTimeSet() {
		return this.timeSet;
	}
	
	public String getInsult() {
		return insult;
	}

	public void setInsult(String insult) {
		this.insult = insult;
	}
	
	public boolean insultAtMaxLength() {
		return this.insult.length() >= MAX_INSULT_LENGTH;
	}
	
	public void appendToInsult(char c) {
		if (!this.insultAtMaxLength())
			this.insult += c;
	}
	
	public void popFromInsult() {
		if (this.insult.length() > 0)
			this.insult = this.insult.substring(0, this.insult.length() - 1);
	}
	
	public void clearInsult() {
		this.insult = new String();
	}
	
	public void sendInsult() {
		if (insult.length() == 0)
			MessageHandler.sendMessage(this, MessageHandler.NULL_SIGNAL);
		
		if (sendingIndex < insult.length())
			MessageHandler.sendMessage(this, insult.charAt(sendingIndex++));
		else
			exit();
	}
	
	public String getHeaderText() {
		if (winner)
			return "To: Loser";
		else
			if (this.phase < 2)
				return "Please wait...";
			else
				return "From: Winner";
	}
	
	public boolean isWinner() {
		return this.winner;
	}
	
	public int getPhase() {
		return this.phase;
	}
	
	public void exit() {
		MessageHandler.closeMessageListener();
		this.ic.cleanUp();
		this.f.waitForPlayers();
	}
	
	@Override
	public Canvas getCanvas() {
		return this.ic;
	}

	@Override
	public ViewID getID() {
		return ViewID.INSULT;
	}

	@Override
	public void transferData(int data) {
		// If the winner sent an empty message, halve the time remaining -- don't exit immediately to shuffle players around
		if (!winner && data == MessageHandler.NULL_SIGNAL) {
			this.timeLeft = (this.timeLeft / 2) + 1;
			return;
		}

		if (data == MessageHandler.GAME_OVER) {
			return;
		}
		
		if (data == MessageHandler.DISCONNECT_SIGNAL) {
			this.f.displayError(ErrorLogic.DISONNECT_MESSAGE);
			return;
		}
		
		if (winner && data == MessageHandler.ACK) {
			sendInsult();
		} else {
			if (this.phase < 2) {
				setTimer(INSULT_DISPLAY_TIME);
				clearInsult();
			}
			appendToInsult((char) data);
			MessageHandler.sendAcknowledge(this);
		}
	}
	
	@Override
	public void transferFail() {
		this.f.displayError(ErrorLogic.TRANSFER_FAIL);
	}
	
	@Override
	public void cleanUp() {
		MessageHandler.sendDisconnect(this);
		MessageHandler.closeMessageListener();
		this.ic.cleanUp();
	}

}
