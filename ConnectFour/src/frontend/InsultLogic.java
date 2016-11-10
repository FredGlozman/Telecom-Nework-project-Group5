package frontend;

public class InsultLogic implements ViewController, MiddleWare {

	protected static final int MAX_INSULT_LENGTH = 32; // Maximum number of characters the insult can contain
	protected static final int INSULT_TIME_LIMIT = 18; // Number of seconds the user has to type up their insult
	protected static final int INSULT_DISPLAY_TIME = 7; // Number of seconds the loser is forced to see the insult for
	protected static final int LOSER_EXTRA_TIME = 2; // Number of additional seconds for the loser to mix things up
	protected static final int LEEWAY_TIME = 4; // Number of seconds granted to receive an acknowledge before quitting
	
	private InsultCanvas ic;
	private WindowFrame f;
	private boolean winner;
	private String insult;
	private int timeLeft, timeSet;
	private int phase;
	
	private MessageHandler mh;
	
	public InsultLogic(WindowFrame f, boolean winner) {
		this.f = f;
		this.winner = winner;
		this.insult = new String();
		this.phase = 0;
		this.mh = NetworkConfiguration.getMessageHandler();
		this.mh.listen(this);
		this.setTimer(INSULT_TIME_LIMIT);
		this.ic = new InsultCanvas(this);
	}
	
	private void setTimer(int seconds) {
		this.phase++;
		this.timeLeft = this.timeSet = (seconds + (winner ? 0 : LOSER_EXTRA_TIME)) * GameCanvas.FPS;
	}
	
	// returns time left
	public int updateTimer() {
		if (--this.timeLeft == 0) {
			if (this.winner)
				this.mh.sendMessage(this, MessageHandler.NULL_SIGNAL);
			else
				exit();
		}
		
		if (--this.timeLeft == -LEEWAY_TIME)
			exit();
		
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
		this.ic.stop();
		
		if (insult.length() == 0) {
			this.mh.sendMessage(this, MessageHandler.NULL_SIGNAL);
			return;
		}
		
		for (int sendingIndex = 0; sendingIndex < insult.length(); sendingIndex++)
			this.mh.sendMessage(this, insult.charAt(sendingIndex));
		
		this.mh.sendMessage(this, MessageHandler.END_OF_STRING);		
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
		this.mh.close();
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
			this.mh.sendMessage(this, MessageHandler.ACK);
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
		
		if (!winner && data == MessageHandler.END_OF_STRING) {
			this.mh.sendMessage(this, MessageHandler.ACK);
			this.ic.resume();
			return;
		}
		
		if (winner && data == MessageHandler.ACK) {
			exit();
			return;
		}
		
		if (this.phase < 2) {
			this.ic.stop();
			setTimer(INSULT_DISPLAY_TIME);
			clearInsult();
		}
		
		appendToInsult((char) data);
	}
	
	@Override
	public void transferFail() {
		this.f.displayCriticalError(ErrorLogic.TRANSFER_FAIL);
	}
	
	@Override
	public void cleanUp() {
		this.mh.close();
		this.ic.cleanUp();
	}

	@Override
	public void disconnect() {
		this.mh.sendMessage(this, MessageHandler.DISCONNECT_SIGNAL);
		try {
			Thread.sleep(MessageHandler.GRACE_PERIOD * 1000);
		} catch (InterruptedException e) {
			// whatever...
		}
	}
}
