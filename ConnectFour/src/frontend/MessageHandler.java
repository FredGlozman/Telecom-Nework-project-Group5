package frontend;

import backend.*;

public class MessageHandler {
	
	private static final int ACK = 931;
	
	public static MessageListener ml;
	
	public static void sendMessage(int message) {
		new MessageTransmitter(NetworkConfiguration.IP, message, NetworkConfiguration.PORT_1).start();
	}

	public static void sendAcknowledge() {
		sendMessage(ACK);
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
