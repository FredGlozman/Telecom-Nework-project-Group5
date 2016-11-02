package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import frontend.MiddleWare;
/**
 
 * @author Group five- Telecom Network project
 * This class handles message receiption
 * All TCP connections
 */
public class MessageListener extends Thread {
	private int port; 
	private ServerSocket listenerSocket;
	private MiddleWare mw;

	public MessageListener(int port, MiddleWare mw) {
		this.port = port;
		this.mw = mw;
		
		try {
			listenerSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	@Override
	public void run() {
		Socket clientSocket;
		try {
			while ((clientSocket = listenerSocket.accept()) != null) {
				InputStream input = clientSocket.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
				int message = reader.read();
				
				mw.transferData(message);
			}
		} catch (IOException e) {
			// throw new RuntimeException(e);
		} finally {
			//close connection once not needed
			close();
		}
	}

	public void close() {
		try {
			if (listenerSocket != null) {
				listenerSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
