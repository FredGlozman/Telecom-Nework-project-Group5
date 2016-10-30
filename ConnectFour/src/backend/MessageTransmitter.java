package backend;

import java.io.IOException;
import java.net.Socket;

import frontend.MiddleWare;

public class MessageTransmitter extends Thread {
	private int port_num;
	private int message;
	private String host_name;
	private MiddleWare mw;

	public MessageTransmitter(String hostName, int message, int port, MiddleWare mw) {
		this.port_num = port;
		this.message = message;
		this.host_name = hostName;
		this.mw = mw;
	}

	@Override
	public void run() {
		Socket transmit_socket = null;
		
		try {
			transmit_socket = new Socket(host_name, port_num);
			transmit_socket.getOutputStream().write(message);
		} catch (IOException e) {
			mw.transferFail();
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
