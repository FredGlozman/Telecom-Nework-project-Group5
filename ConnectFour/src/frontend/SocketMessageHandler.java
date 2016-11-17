package frontend;

import backend.*;

/**
 * Message handler for the socket scenario. Is used by the front-end to transmit outgoing and listen to incoming messages.
 */
public class SocketMessageHandler implements MessageHandler {
	
	private SocketMessageTransmitter mt;
	private SocketMessageListener ml;
	
	private String ip;
	private int port1;
	private int port2;
	
	/**
	 * Constructor: sets up fields.
	 * @param hostname IP address of the opponent as a string.
	 * @param port1 Transmitting port number.
	 * @param port2 Listening port number.
	 */
	public SocketMessageHandler(String hostname, int port1, int port2) {
		this.ip = hostname;
		this.port1 = port1;
		this.port2 = port2;
	}

	@Override
	public void sendMessage(MiddleWare mw, int message) {
		if (mt == null) {
			mt = new SocketMessageTransmitter(ip, port1, mw);
			mt.start();
		} else {
			mt.setMiddleWare(mw);
		}
		
		mt.send(message);
	}

	@Override
	public void listen(MiddleWare mw) {
		if (ml == null) {
			ml = new SocketMessageListener(port2, mw);
			ml.start();
		} else {
			ml.setMiddleWare(mw);
		}			
	}

	@Override
	public void close() {
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
