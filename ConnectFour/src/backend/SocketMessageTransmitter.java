package backend;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

import frontend.MiddleWare;

/**
 * Handles socket message transmission. All connections are TCP.
 */
public class SocketMessageTransmitter extends Thread {
	protected static final int SOCKET_WRITE_SLEEP_TIME_MS = 10;
	
	private Socket senderSocket;
	private MiddleWare mw;
	private Queue<Integer> messageQueue;

	/**
	 * Constructor: initializes this transmitting socket at a given port and destined to a specific hostname.
	 * @param hostName Hostname messages will be transmitted to (opponent's IPv4 address).
	 * @param port Port number on which socket will be opened.
	 * @param mw Means of communicating the information with the front-end.
	 */
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
	
	/**
	 * In case the view changes and the same message transmitter is needed, use this method.
	 * @param mw New means of interfacing with the front-end.
	 */
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	
	/**
	 * Constantly check the message queue, if it is not empty, write it to the transmission stream and
	 * flush it. Then wait and try again. Stop when the transmission socket is closed.
	 */
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
				
				// Very often, but not too often.
				Thread.sleep(SOCKET_WRITE_SLEEP_TIME_MS);
			}
		} catch (InterruptedException e) {
			// oh well...
		} catch (Exception e) {
			error();
		} finally {
			// Always close the transmission socket.
			close();
		}
	}
	
	/**
	 * Function to be called when a message is to be sent. Queues it up for transmission.
	 * @param message Message to be transmitted.
	 */
	public void send(int message) {
		// If there is no socket to write to, error out.
		if (this.senderSocket == null) {
			error();
			return;
		}
		
		this.messageQueue.offer(message);
	}
	
	/**
	 * Finish emptying up the queue and close the socket.
	 */
	public void close() {
		try {
			if (this.senderSocket != null) {
				while (!this.messageQueue.isEmpty());
				this.senderSocket.close();
				this.senderSocket = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * If something goes horribly wrong, clear the queue and notify the user.
	 */
	private void error() {
		this.messageQueue.clear();
		this.mw.transferFail();
	}
}
