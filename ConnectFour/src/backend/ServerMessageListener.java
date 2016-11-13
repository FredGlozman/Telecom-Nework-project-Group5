package backend;

import frontend.MiddleWare;

/**
 * Server fallback message listener. This interfaces with the server listening file, i.e. whenever
 * the opponent posts a message, it will be through this file. This replaces the listening socket. The file
 * is created to be hosted on Fred Glozman's SOCS server. It is removed after use.
 */
public class ServerMessageListener extends Thread {
	protected static final int FILE_READ_SLEEP_TIME_MS = 50;
	
	private MiddleWare mw;
	private String listeningFileName;
	private boolean isOpen;
	
	/**
	 * Constructor: sets up the file by creating it.
	 * @param listeningFileName Name of the file to be created.
	 * @param mw Means of communicating the information with the front-end.
	 */
	public ServerMessageListener(String listeningFileName, MiddleWare mw) {
		this.listeningFileName = listeningFileName;
		this.mw = mw;
		this.isOpen = false;
		
		// Create file on server.
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		try {
			if (file.exists(listeningFileName))
				throw new RuntimeException("File being created already exists!");
			
			file.createFile(listeningFileName);
			while (!file.exists(listeningFileName));
			this.isOpen = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * In case the view changes and the same message listener is needed, use this method.
	 * @param mw New means of interfacing with the front-end.
	 */
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}

	/**
	 * Constantly ping the server file to check whether the opponent has posted anything to the file.
	 * If so, then notify the front-end via mw.
	 * Stop when the file is closed via the close() method.
	 */
	@Override
	public void run() {
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		try {
			while (this.isOpen) {
				String message = file.read(this.listeningFileName);
				
				if (message != null && message.length() > 0) {
					file.removeLine(this.listeningFileName, message);
					// Since reading from the server returns a string and message are integers by design, parse the integer.
					mw.transferData(Integer.parseInt(message));
				}
				
				// Ensure that the server isn't being pinged too often, which is excessive and may consume too many resources.
				Thread.sleep(FILE_READ_SLEEP_TIME_MS);
				
			}
		} catch (InterruptedException e) {
			// oh well...
		} catch (Exception e) {
			// do nothing -- i.e. close()
		} finally {
			// Ensure the file is closed no matter what in order not to clutter the server much.
			close();
		}
	}
	
	/**
	 * Remove the listening file from the server.
	 */
	public void close() {
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		synchronized (mw) {
			if (file.exists(this.listeningFileName))
				file.delete(this.listeningFileName);
		}
		
		this.isOpen = false;
	}
}