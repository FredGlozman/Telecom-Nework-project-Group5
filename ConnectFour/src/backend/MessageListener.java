package backend;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import frontend.MiddleWare;
/**
 
 * @author Group five- Telecom Network project
 * This class handles message receiption
 * All TCP connections
 */
public class MessageListener extends Thread {
	private ServerSocket listenerSocket;
	private MiddleWare mw;

	public MessageListener(int port, MiddleWare mw) {
		this.mw = mw;
		
		try {
			this.listenerSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	
	@Override
	public void run() {
		try {
			Socket clientSocket = this.listenerSocket.accept();
			InputStream in = clientSocket.getInputStream();
			while (this.listenerSocket != null) {
				int message = in.read();
				if (message == -1)
					return;
				mw.transferData(message);
			}
		} catch (Exception e) {
			// do nothing
		} finally {
			//close connection once not needed
			close();
		}
	}

	public void close() {
		try {
			if (listenerSocket != null) {
				this.listenerSocket.close();
				this.listenerSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
