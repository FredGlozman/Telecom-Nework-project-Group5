package backend;

import java.io.IOException;
import java.net.Socket;

import frontend.MiddleWare;
/**
 * 
 *This class handles message transmission
 *TCP connection
 *an instance of this class is needed for every transmission
 *
 */
public class MessageTransmitter {

	public static void sendMessage(String hostName, int message, int port, MiddleWare mw) {
		Socket transmit_socket = null;
		
		try {
			transmit_socket = new Socket(hostName, port);
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
