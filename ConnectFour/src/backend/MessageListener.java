package backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import frontend.MiddleWare;

public class MessageListener extends Thread {
	private int port_num; 
	private ServerSocket listener_socket;
	private MiddleWare mw;

	public MessageListener(int port, MiddleWare mw) {
		this.port_num = port;
		this.mw = mw;
		
		try {
			listener_socket = new ServerSocket(port_num);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}

	public void run() {
		Socket clientSocket;
		try {
			while ((clientSocket = listener_socket.accept()) != null) {
				InputStream input = clientSocket.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(input));
				int message = reader.read();
				
				mw.transferData(message);
			}
		} catch (IOException e) {
//			throw new RuntimeException(e);
		} finally {
			close();
		}
	}

	public void close() {
		try {
			if (listener_socket != null) {
				listener_socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
