package backend;

import java.util.LinkedList;
import java.util.List;

import frontend.PoolObserver;

public class PlayerPool {
	private final ServerTextFileIO file;
	private final List<Player> pool;
	
	private final PoolObserver observer;
	
	public PlayerPool(PoolObserver observer) {
		this.observer = observer;
		
		file = ServerTextFileIO.getInstance();
		pool = new LinkedList<Player>();
	}
			
	/**
	 * Add yourself to the player pool. 
	 * Will call back the observer when ready for game-play.
	 */
	public void addSelf() {
		Thread adder = new Thread() {
			public void run() {	
				waitForAvailability(); //waits until there are less than 2 players in the pool
						
				if(pool.size() == 0) {
					Player me = new Player();
					file.addLine(me.toString());
					
					listen(me);
				} else if(pool.size() == 1) {
					Player opponent = pool.get(0);
					Player me = new Player(opponent);
					file.addLine(me.toString());
										
					observer.startGame(me, opponent);
				} else {
					throw new RuntimeException("An error occured. More than 1 player is in the Player Pool. Can't add player yet. Should have been waiting.");
				}
		    }  
		};

		adder.start();
	}
	
	/**
	 * Wait for second player to connect and post their player info to the server
	 * @param me my player info
	 */
	private void listen(Player me) {
		Thread listener = new Thread() {
			public void run() {
		    	Player opponent = null; 

		    	boolean first = true;
		    	do {
			    	if(!first) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						
						first = false;
			    	}

		    		reloadPool();
		    		
			    	for(Player player : pool) {
			    		if(player.getCoin() != me.getCoin()) {
			    			opponent = player;
			    			break;
			    		}
			    	}
		    	} while(opponent == null);
		    			    	
		    	file.clear();
		    			    	
		    	observer.startGame(me, opponent);
		    }  
		};

		listener.start();
	}
	
	/**
	 * Puts this thread to sleep while the Player Pool has 2 or more 
	 * @return content of the file once it contains less than 2 players
	 */
	private void waitForAvailability() {	
		boolean first = true;
		do {
			if(!first) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				first = false;
			}

			reloadPool();
		} while(pool.size() >= 2);
	}	
	
	/**
	 * Reloads the player pool from the player info in the file on the server
	 */
	private void reloadPool() {
		pool.clear();
		
		String content = file.read();
		if(content != null && content.length()>0) {
			for(String line : content.split("\n")) {
				if(line != null && line.length() > 0 && line.split(",").length == 2) {
					pool.add(new Player(line));
				}
			}
		}
	}
}
