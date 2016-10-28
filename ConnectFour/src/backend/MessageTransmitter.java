package backend;

import java.io.IOException;
import java.net.Socket;

public class MessageTransmitter extends Thread {
	private int port_num;
	private int message;
	private String host_name;

	public MessageTransmitter(String hostName, int message, int port) {
		this.port_num = port;
		this.message = message;
		this.host_name = hostName;
	}

	@Override
	public void run() {
		Socket transmit_socket = null;
		
		try {
			transmit_socket = new Socket(host_name, port_num);
			transmit_socket.getOutputStream().write(message);
			System.out.println("message that was sent is: " + message);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if(transmit_socket != null) {
				try {
					transmit_socket.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
