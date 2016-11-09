package backend;

import frontend.MiddleWare;

public class ServerMessageListener extends Thread {
	private MiddleWare mw;
	
	public ServerMessageListener(MiddleWare mw) {
		this.mw = mw;
	}
	
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	
	@Override
	public void run() {
		
	}
	
	public void close() {
		
	}
}