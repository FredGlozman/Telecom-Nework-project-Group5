package backend;

import java.util.ArrayDeque;
import java.util.Queue;

import frontend.MiddleWare;

public class ServerMessageTransmitter extends Thread {
	private MiddleWare mw;
	private Queue<Integer> messageQueue;
	
	public ServerMessageTransmitter(MiddleWare mw) {
		this.mw = mw;
		this.messageQueue = new ArrayDeque<Integer>();
	}
	
	private void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	
	@Override
	public void run() {
		
	}
	
	public void close() {
		
	}
}
