package network;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MessageTransmitter extends Thread {
	int port_num;
	int message;
	String host_name;
	
	
	
	public MessageTransmitter(String hostName, int message, int port){
		this.port_num=port;
		this.message=message;
		this.host_name=hostName;
		
		
	}
	
	
	@Override
	public void run(){
		try {
				Socket transmit_socket=new Socket(host_name, port_num);
			transmit_socket.getOutputStream().write(message);
			System.out.println("message that was sent is: "+message);
			transmit_socket.close();

			
		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}
	

}
