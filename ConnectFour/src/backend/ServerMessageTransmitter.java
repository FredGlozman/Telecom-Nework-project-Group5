package backend;

import java.util.ArrayDeque;
import java.util.Queue;

import frontend.MiddleWare;

/**
 * Server fallback message transmitter. This interfaces with the server transmitting file, i.e. whenever
 * the user needs to post a message, it will be through this file. The replaces the transmitting socket.
 * This file is created (and deleted) by the opponent upon server message listener setup.
 */
public class ServerMessageTransmitter extends Thread {
	protected static final int FILE_CHECK_TIME_LIMIT = 7; // Time in seconds
	protected static final int FILE_CHECK_SLEEP_TIME_MS = 500; // Time inbetween checks in milliseconds
	protected static final int FILE_WRITE_SLEEP_TIME_MS = 100;
	
	private MiddleWare mw;
	private Queue<Integer> messageQueue;
	private String writingFileName;
	private boolean isOpen;
	
	/**
	 * Constructor: initializes all fields and waits for the file to exists, which means the opponent is ready.
	 * @param writingFileName Name of the file the opponent is supposed to create.
	 * @param mw Means of communicated the information with the front-end.
	 */
	public ServerMessageTransmitter(String writingFileName, MiddleWare mw) {
		this.writingFileName = writingFileName;
		this.mw = mw;
		// Using a queue in case a message is pending upon reception of another.
		this.messageQueue = new ArrayDeque<Integer>();
		this.isOpen = false;
		
		int fileCheckTimeLimitMs = FILE_CHECK_TIME_LIMIT * 1000;
		int timeElapsedMs = 0;
		
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		try {
			// Wait for the file to exist.
			while (!file.exists(writingFileName)) {
				// If the file doesn't exist for too long, there is an important error.
				if (timeElapsedMs >= fileCheckTimeLimitMs)
					throw new RuntimeException("Timed out while waiting for file to exist.");
				
				Thread.sleep(FILE_CHECK_SLEEP_TIME_MS);
				timeElapsedMs += FILE_CHECK_SLEEP_TIME_MS;
			}
			
			this.isOpen = true;
		} catch (InterruptedException e) {
			// whatever...
		} catch (Exception e) {
			mw.transferFail();
		}
	}
	
	/**
	 * In case the view changes and the same message transmitter is needed, use this method.
	 * @param mw New means of interfacing with the front-end.
	 */
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	

	@Override
	public void run() {
		// Constantly check the message queue, if it is not empty, check if the server is ready to take
		// a new message, if it isn't wait and try again later, and if it is, then post the message to the
		// server.
		 
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		try {
			while (this.isOpen) {
				synchronized (mw) {
					boolean areElementsQueued = !this.messageQueue.isEmpty();
					
					// If opponent is gone.
					if (!file.exists(this.writingFileName)) {
						if (areElementsQueued)
							throw new RuntimeException("Writing file no longer exists.");
						close();
					}
					
					if (areElementsQueued && file.read(this.writingFileName).length() == 0) {
						int message = this.messageQueue.poll();
						file.addLine(this.writingFileName, new Integer(message).toString());
					}
				}
				Thread.sleep(FILE_WRITE_SLEEP_TIME_MS);
			}
		} catch (InterruptedException e) {
			// oh well...
		} catch (Exception e) {
			// If something goes wrong, clear the queue and notify the user.
			error();
		} finally {
			close();
		}
	}
	
	/**
	 * Function to be called when a message is to be sent. Queues it up for transmission.
	 * @param message Message to be transmitted.
	 */
	public void send(int message) {
		// If there is no server to write to, error out.
		if (!this.isOpen) {
			error();
			return;
		}
		
		this.messageQueue.offer(message);
	}
	
	/**
	 * Finish emptying up the queue and stop the process.
	 */
	public void close() {
		while (!this.messageQueue.isEmpty());
		this.isOpen = false;
	}
	
	/**
	 * If something goes horribly wrong, clear the queue and notify the user.
	 */
	private void error() {
		this.messageQueue.clear();
		this.mw.transferFail();
	}
}
