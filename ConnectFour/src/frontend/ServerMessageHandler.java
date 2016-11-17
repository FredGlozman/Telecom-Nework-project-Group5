package frontend;

import backend.ServerMessageListener;
import backend.ServerMessageTransmitter;

/**
 * Message handler for the server fallback mechanism. Is used by the front-end to transmit outgoing and listen to incoming messages.
 */
public class ServerMessageHandler implements MessageHandler {

	private ServerMessageTransmitter mt;
	private ServerMessageListener ml;
	
	private String writingFileName;
	private String readingFileName;
	
	/**
	 * Constructor: sets up fields.
	 * @param writingFileName Name of the file to which the player will write.
	 * @param readingFileName Name of the file from which the player will read.
	 */
	public ServerMessageHandler(String writingFileName, String readingFileName) {
		this.writingFileName = writingFileName;
		this.readingFileName = readingFileName;
	}
	
	@Override
	public void sendMessage(MiddleWare mw, int message) {
		if (mt == null) {
			mt = new ServerMessageTransmitter(writingFileName, mw);
			mt.start();
		} else {
			mt.setMiddleWare(mw);
		}
		
		mt.send(message);
	}

	@Override
	public void listen(MiddleWare mw) {
		if (ml == null) {
			ml = new ServerMessageListener(readingFileName, mw);
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
