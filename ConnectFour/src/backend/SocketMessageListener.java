package backend;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import frontend.MiddleWare;
/**
 * Handles socket message reception. All connections are TCP.
 */
public class SocketMessageListener extends Thread {
	protected static final int SOCKET_READ_SLEEP_TIME_MS = 10;
	
	private ServerSocket listenerSocket;
	private MiddleWare mw;

	/**
	 * Constructor: initializes the listening socket at a given port.
	 * @param port Port number at which the socket will reside.
	 * @param mw Means of communicating the information with the front-end.
	 */
	public SocketMessageListener(int port, MiddleWare mw) {
		this.mw = mw;
		
		try {
			this.listenerSocket = new ServerSocket(port);
		} catch (IOException e) {
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
	
	@Override
	public void run() {
		// Constantly check whether the opponent sent anything. If they did, notify the front-end via mw.
		// Stop when the file is closed via the close() method.

		try {
			Socket clientSocket = this.listenerSocket.accept();
			InputStream in = clientSocket.getInputStream();
			while (this.listenerSocket != null) {
				int message = in.read();
				
				// -1 indicates transmitting end of the socket has been closed.
				if (message == -1)
					throw new RuntimeException("Opponent's transmission socket has been closed.");
				mw.transferData(message);
				
				// Check very often, but not as often as the machine can.
				Thread.sleep(SOCKET_READ_SLEEP_TIME_MS);
			}
		} catch (InterruptedException e) {
			// oh well...
		} catch (Exception e) {
			// do nothing - i.e. close the listener.
		} finally {
			// close connection once not needed
			close();
		}
	}

	/**
	 * Close the listening socket to free the port.
	 */
	public void close() {
		try {
			if (listenerSocket != null) {
				this.listenerSocket.close();
				this.listenerSocket = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
