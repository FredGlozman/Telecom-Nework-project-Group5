package backend;

import java.util.ArrayDeque;
import java.util.Queue;

import frontend.MiddleWare;

public class ServerMessageTransmitter extends Thread {
	protected static final int FILE_CHECK_TIME_LIMIT = 20; // Time in seconds
	protected static final int FILE_CHECK_SLEEP_TIME_MS = 500; // Time inbetween checks in milliseconds
	protected static final int FILE_WRITE_SLEEP_TIME_MS = 100;
	
	private MiddleWare mw;
	private Queue<Integer> messageQueue;
	private String writingFileName;
	private boolean isOpen;
	
	public ServerMessageTransmitter(String writingFileName, MiddleWare mw) {
		this.writingFileName = writingFileName;
		this.mw = mw;
		this.messageQueue = new ArrayDeque<Integer>();
		this.isOpen = false;
		
		int fileCheckTimeLimitMs = FILE_CHECK_TIME_LIMIT * 1000;
		int timeElapsedMs = 0;
		
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		while (!file.exists(writingFileName)) {
			if (timeElapsedMs >= fileCheckTimeLimitMs)
				throw new RuntimeException("Timed out while waiting for file to exist.");
			
			try {
				Thread.sleep(FILE_CHECK_SLEEP_TIME_MS);
			} catch (InterruptedException e) {
				// whatever...
			}
			timeElapsedMs += FILE_CHECK_SLEEP_TIME_MS;
		}
		
		this.isOpen = true;
	}
	
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	
	@Override
	public void run() {
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		try {
			while (this.isOpen) {
				synchronized (mw) {
					boolean areElementsQueued = !this.messageQueue.isEmpty();
					
					if (!file.exists(this.writingFileName)) {
						if (areElementsQueued)
							throw new RuntimeException("Writing file no longer exists.");
						close();
					}
					
					if (areElementsQueued && file.read(this.writingFileName).length() == 0) {
						int message = this.messageQueue.poll();
						System.out.println("Outgoing message: " + ((char) message) + "/" + message);
						file.addLine(this.writingFileName, new Integer(message).toString());
					}
				}
				Thread.sleep(FILE_WRITE_SLEEP_TIME_MS);
			}
		} catch (InterruptedException e) {
			// oh well...
		} catch (Exception e) {
			this.messageQueue.clear();
			this.mw.transferFail();
		} finally {
			close();
		}
	}
	
	public void send(int message) {
		this.messageQueue.offer(message);
	}
	
	public void close() {
		while (!this.messageQueue.isEmpty());
		this.isOpen = false;
	}
}
