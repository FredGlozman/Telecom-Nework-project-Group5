package frontend;

public interface MessageHandler {
	static final int GRACE_PERIOD = 1; // Time in seconds to send a disconnect signal
	
	static final int GAME_OVER = 22;
	static final int ACK = 21;
	static final int DISCONNECT_SIGNAL = 20;
	static final int NULL_SIGNAL = 19;
	static final int END_OF_STRING = 18;
	static final int TIME_OUT_SYNC = 8;
	
	public void sendMessage(MiddleWare mw, int message);
	public void listen(MiddleWare mw);
	public void close();
}
