package frontend;

import backend.*;

public class MessageHandler {
	
	protected static final int GAME_OVER = 22;
	protected static final int ACK = 21;
	protected static final int DISCONNECT_SIGNAL = 20;
	
	public static MessageListener ml;
	
	public static void sendMessage(MiddleWare mw, int message) {
		new MessageTransmitter(NetworkConfiguration.IP, message, NetworkConfiguration.PORT_1, mw).start();
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

	public static void closeMessageListener() {
		ml.close();
		ml = null;
	}
}
