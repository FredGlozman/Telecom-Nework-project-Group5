package backend;

import java.util.LinkedList;
import java.util.List;

import frontend.PoolObserver;

public class PlayerPool {
	private final ServerTextFileIO file;
	private final List<Player> pool;
	private Player self;
  
	private static final String FILE_NAME = "PlayerPool.txt";

	private static PlayerPool instance; 
	
	/**
	 * @param observer will get notified when a match is made 
	 */
	private PlayerPool() {		
		file = ServerTextFileIO.getInstance();
		pool = new LinkedList<Player>();
	}
	
	public static PlayerPool getInstance() {
		if(instance == null) {
			instance = new PlayerPool();
		} 
		
		return instance;
	}
	
	public void removeSelf() {
		if(self != null) {
			file.removeLine(FILE_NAME, self.toString());
		}
	}
			
	/**
	 * Add yourself to the player pool.
	 * Can only join the player pool if there is less than 2 players already in the pool. 
	 * If there are 2 players in the pool, need to wait before adding yourself to the pool. 
	 * Will call back the observer when a match is made and are ready for game-play.
	 */
	public void addSelf(PoolObserver observer) {
		Thread adder = new Thread() {
			public void run() {	
				waitForAvailability(); //waits until there are less than 2 players in the pool
						
				//no player in the pool. add yourself to the pool.
				if(pool.size() == 0) {
					Player me = new Player();
					file.addLine(FILE_NAME, me.toString());
					
					self = me;
					listen(observer, me); //listen for another player to join the pool
				} 
				//1 player is already in the pool. 
				//add yourself to the pool but make sure your coin value is complementary to the player in the pool.
				else if(pool.size() == 1) {
					Player opponent = pool.get(0);
					Player me = new Player(opponent);
					file.addLine(FILE_NAME, me.toString());
					
					self = me;
					observer.startGame(me, opponent);
				} 
				//an error occurred. should have been waiting 
				else {
					throw new RuntimeException("An error occured. More than 1 player is in the Player Pool. Can't add player yet. Should have been waiting.");
				}
		    }  
		};

		adder.start();
	}
	
	/**
	 * Wait for second player to connect
	 * @param me my player info
	 */
	private void listen(PoolObserver observer, Player me) {
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
		    			    	
		    	file.removeLines(FILE_NAME, new String[]{me.toString(), opponent.toString()});
		    			
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
		
		String content = file.read(FILE_NAME);
		if(content != null && content.length()>0) {
			for(String line : content.split("\n")) {
				if(line != null && line.length() > 0 && line.split(",").length == 2) {
					pool.add(new Player(line));
				}
			}
		}
	}
}
