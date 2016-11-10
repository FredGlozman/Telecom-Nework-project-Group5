package backend;

import frontend.MiddleWare;

public class ServerMessageListener extends Thread {
	private MiddleWare mw;
	private String listeningFileName;
	private boolean isOpen;
	
	public ServerMessageListener(String listeningFileName, MiddleWare mw) {
		this.listeningFileName = listeningFileName;
		this.mw = mw;
		this.isOpen = false;
		
		
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		try {
			if (file.exists(listeningFileName))
				throw new RuntimeException("File being created already exists!");
			
			file.createFile(listeningFileName);
			while (!file.exists(listeningFileName));
			this.isOpen = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setMiddleWare(MiddleWare mw) {
		this.mw = mw;
	}
	
	@Override
	public void run() {
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		while (this.isOpen) {
			String message = file.read(this.listeningFileName);
			if (message != null && message.length() > 0) {
				file.removeLine(this.listeningFileName, message);
				mw.transferData(Integer.parseInt(message));
			}
		}
	}
	
	public void close() {
		ServerTextFileIO file = ServerTextFileIO.getInstance();
		
		file.delete(this.listeningFileName);
		this.isOpen = false;
	}
}