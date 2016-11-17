package frontend;

/**
 * Insult controller: Repsonsible for the insult logic.
 */
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
	
	/**
	 * Constructor: uses previously established network configuration, sets up the timer, and launches the view.
	 * @param f Pointer to the frame containing the view.
	 * @param winner Whether the player is the winner.
	 */
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
	
	/**
	 * Reset the timer to a given time and increment the phase (for the loser: phase 1 = waiting for insult; phase 2 = insult received).
	 * @param seconds Number of seconds to reset the timer to.
	 */
	private void setTimer(int seconds) {
		this.phase++;
		this.timeLeft = this.timeSet = (seconds + (winner ? 0 : LOSER_EXTRA_TIME)) * GameCanvas.FPS;
	}

	/**
	 * Decrement the timer at every frame and act appropriately if no time remains.
	 * @return Time left in number of frames.
	 */
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
	
	/**
	 * Getter function to get the timer cap.
	 * @return To how many frames the timer was reset last.
	 */
	public int getTimeSet() {
		return this.timeSet;
	}
	
	/**
	 * Getter function for the insult as a string.
	 * @return Insult as a string.
	 */
	public String getInsult() {
		return insult;
	}

	/**
	 * Setter function for the insult as a string.
	 * @param insult Insult as a string.
	 */
	public void setInsult(String insult) {
		this.insult = insult;
	}
	
	/**
	 * Returns whether the insult is at the maximum character count.
	 * @return True if the insult is at the maximum character count; false otherwise.
	 */
	public boolean insultAtMaxLength() {
		return this.insult.length() >= MAX_INSULT_LENGTH;
	}
	
	/**
	 * Append one character to the insult if there is room for it - called when the winner types or when the loser receives a byte.
	 * @param c Character to append.
	 */
	public void appendToInsult(char c) {
		if (!this.insultAtMaxLength())
			this.insult += c;
	}
	
	/**
	 * Remove the last character from the insult - called when the winner backspaces.
	 */
	public void popFromInsult() {
		if (this.insult.length() > 0)
			this.insult = this.insult.substring(0, this.insult.length() - 1);
	}
	
	/**
	 * Clear the insult - called when the winner hits escape.
	 */
	public void clearInsult() {
		this.insult = new String();
	}
	
	/**
	 * Send the insult byte-by-byte (character-by-character).
	 */
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
	
	/**
	 * Getter function for the header to display in the view.
	 * @return Header text as a string.
	 */
	public String getHeaderText() {
		if (winner)
			return "To: Loser";
		else
			if (this.phase < 2)
				return "Please wait...";
			else
				return "From: Winner";
	}
	
	/**
	 * Returns whether the player is the winner.
	 * @return True if the player is the winner; false otherwise.
	 */
	public boolean isWinner() {
		return this.winner;
	}
	
	/**
	 * Getter function for the phase (for the loser: phase 1 = waiting for insult; phase 2 = reading the insult).
	 * @return Phase number (1 or 2); always 1 for winner.
	 */
	public int getPhase() {
		return this.phase;
	}
	
	/**
	 * Move onto the player queue.
	 */
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
			// Give some time for the signal to go out.
			Thread.sleep(MessageHandler.GRACE_PERIOD * 1000);
		} catch (InterruptedException e) {
			// If the signal still didn't go out, the other player will have to wait for their timer to run out, and they will know then.
			// No big deal.
		}
	}
}
