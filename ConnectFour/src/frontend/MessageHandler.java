package frontend;

/**
 * Wrapper to handle messages in the front-end; abstracts the networking away.
 */
public interface MessageHandler {
	static final int GRACE_PERIOD = 1; // Time in seconds to send a disconnect signal
	
	static final int CHECK_NUMBER_LOW = 30; // Must be greater than all other signal numbers.
	static final int GAME_OVER = 22;
	static final int ACK = 21;
	static final int DISCONNECT_SIGNAL = 20;
	static final int NULL_SIGNAL = 19;
	static final int END_OF_STRING = 18;
	static final int TIME_OUT_SYNC = 8;

	
	/**
	 * If no transmitting connection is established, establish it.
	 * Transmit a message over the connection.
	 * @param mw Source front-end component.
	 * @param message Message to transmit.
	 */
	public void sendMessage(MiddleWare mw, int message);
	
	/**
	 * If no listening connection is established, establish it.
	 * Listen to messages, linking a front-end component to react to them.
	 * @param mw Destination front-end component.
	 */
	public void listen(MiddleWare mw);
	
	/**
	 * Close all connections (transmitting and listening).
	 */
	public void close();
}
