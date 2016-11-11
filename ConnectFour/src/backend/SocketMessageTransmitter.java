package backend;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

import frontend.MiddleWare;
/**
 * 
 *This class handles message transmission
 *TCP connection
 *an instance of this class is needed for every transmission
 *
 */
public class SocketMessageTransmitter extends Thread {
	protected static final int SOCKET_WRITE_SLEEP_TIME_MS = 10;
	
	private Socket senderSocket;
	private MiddleWare mw;
	private Queue<Integer> messageQueue;

	public SocketMessageTransmitter(String hostName, int port, MiddleWare mw) {
		this.mw = mw;
		this.messageQueue = new ArrayDeque<Integer>();

		this.senderSocket = null;
		try {
			this.senderSocket = new Socket(hostName, port);
		} catch (Exception e) {
			mw.transferFail();
		}
	}
	
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	
	@Override
	public void run() {
		try {
			OutputStream out = this.senderSocket.getOutputStream();
			while (this.senderSocket != null) {
				synchronized (mw) {
					if (!this.messageQueue.isEmpty()) {
						int message = this.messageQueue.poll();
						out.write(message);
						out.flush();
					}					
				}
				
				Thread.sleep(SOCKET_WRITE_SLEEP_TIME_MS);
			}
		} catch (InterruptedException e) {
			// oh well...
		} catch (Exception e) {
			this.messageQueue.clear();
			mw.transferFail();
		} finally {
			close();
		}
	}
	
	public void send(int message) {
		this.messageQueue.offer(message);
	}
	
	public void close() {
		try {
			if (this.senderSocket != null) {
				while (!this.messageQueue.isEmpty());
				this.senderSocket.close();
				this.senderSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
