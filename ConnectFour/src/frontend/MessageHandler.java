package frontend;

import backend.*;

public class MessageHandler {
	
	protected static final int GRACE_PERIOD = 1; // Time in seconds to send a disconnect signal
	
	protected static final int GAME_OVER = 22;
	protected static final int ACK = 21;
	protected static final int DISCONNECT_SIGNAL = 20;
	protected static final int NULL_SIGNAL = 19;
	protected static final int END_OF_STRING = 18;
	protected static final int TIME_OUT_SYNC = 8;
	
	public static MessageTransmitter mt;
	public static MessageListener ml;
	
	public static void sendMessage(MiddleWare mw, int message) {
		if (mt == null) {
			mt = new MessageTransmitter(NetworkConfiguration.IP, NetworkConfiguration.PORT_1, mw);
			mt.start();
		} else {
			mt.setMiddleWare(mw);
		}
		
		mt.send(message);
	}

	public static void sendAcknowledge(MiddleWare mw) {
		sendMessage(mw, ACK);
	}
	
	public static void sendDisconnect(MiddleWare mw) {
		sendMessage(mw, DISCONNECT_SIGNAL);
	}
	
	public static MessageListener listen(MiddleWare mw) {
		if (ml == null) {
			ml = new MessageListener(NetworkConfiguration.PORT_2, mw);
			ml.start();
		} else {
			ml.setMiddleWare(mw);
		}
		
		return ml;
			
	}

	public static void closeSockets() {
		if (ml != null) {
			ml.close();
			ml = null;
		}
		
		if (mt != null) {
			mt.close();
			mt = null;
		}
	}
}
