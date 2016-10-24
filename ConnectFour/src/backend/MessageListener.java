package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageListener extends Thread{
	int port_num;		//to be changed to a constant value
	ServerSocket listener_socket;
	
	public MessageListener(int port){
		this.port_num=port;
			try {
				listener_socket=new ServerSocket(port_num);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void run(){
		Socket clientSocket;
		try {
			while((clientSocket=listener_socket.accept())!=null){
				InputStream input=clientSocket.getInputStream();
				BufferedReader reader=new BufferedReader(new InputStreamReader(input));
				int message=reader.read();
				System.out.println("message that was received is: "+message);

				
			}
		} catch (IOException e) {}
		
	}
	
	public void close(){
		try {
			listener_socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

}
