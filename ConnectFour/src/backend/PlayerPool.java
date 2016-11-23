package backend;

import java.util.LinkedList;
import java.util.List;

import frontend.PoolObserver;

/**
 * Singleton class player pool from which opponents will be picked and matches will be made. The player pool
 * is a text file hosted on Fred Glozman's SOCS server where stringified players are inserted. When a player
 * gets in queue, they add themself to the file, and when they find another player and a match is made,
 * they both get removed.
 */
public class PlayerPool {
	private final ServerTextFileIO file;
	private final List<Player> pool;
	private Player self;
	private boolean removedSelf;
  
	protected static final String PLAYER_POOL_FILE_NAME = "PlayerPool.txt";
	protected static final int MAX_AVAILABILITY_WAIT_TIME = 5000; // 5 seconds
	protected static final int AVAILABILITY_WAIT_TIME = 500; // 1/2 seconds
	protected static final long POOL_CHECK_TIMEOUT = 100;

	private static PlayerPool instance; 
	
	/**
	 * Private constructor to enforce singleton pattern.
	 * @param observer will get notified when a match is made 
	 */
	private PlayerPool() {
		file = ServerTextFileIO.getInstance();
		pool = new LinkedList<Player>();
		removedSelf = false;
	}
	
	/**
	 * Following the singleton pattern (lazy evaluation).
	 * @return Instance of PlayerPool.
	 */
	public static PlayerPool getInstance() {
		if (instance == null) {
			instance = new PlayerPool();
		}
		
		return instance;
	}
	
	/**
	 * Remove yourself from the player pool.
	 */
	public void removeSelf() {
		if (self != null) {
			file.removeLine(PLAYER_POOL_FILE_NAME, self.toString());
			removedSelf = true;
		}
	}
			
	/**
	 * Add yourself to the player pool.
	 * Can only join the player pool if there is less than 2 players already in the pool. 
	 * If there are 2 players in the pool, need to wait before adding yourself to the pool. 
	 * Will call back the observer when a match is made and are ready for game-play.
	 */
	public void addSelf(PoolObserver observer) {
		removedSelf = false;
		
		Thread adder = new Thread() {
			public void run() {	
				waitForAvailability(); // Waits until there are less than 2 players in the pool.
						
				// No player in the pool. add yourself to the pool.
				if (pool.size() == 0) {
					Player me = new Player();
					file.addLine(PLAYER_POOL_FILE_NAME, me.toString());
					
					self = me;
					listen(observer);
					
				} 
				// 1 player is already in the pool. 
				// Add yourself to the pool but make sure your coin value is complementary to the player in the pool.
				else if (pool.size() == 1) {
					Player opponent = pool.get(0);
					Player me = new Player(opponent);
					file.addLine(PLAYER_POOL_FILE_NAME, me.toString());
					
					self = me;
					observer.startGame(self, opponent);
				}
				// An error occurred. should have been waiting. 
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
	private void listen(PoolObserver observer) {
		if (self == null)
			throw new RuntimeException("Self is null, should not call listen!");

		Thread listener = new Thread() {
			public void run() {
		    	Player opponent = null; 

		    	do {
					try {
						Thread.sleep(POOL_CHECK_TIMEOUT);
					} catch (InterruptedException e) {
						// whatever...
					}

		    		reloadPool();
		    		
		    		if (pool.size() > 2) {
		    			removeSelf();
		    			addSelf(observer);
		    			return;
		    		}
		    				    		
		    		boolean selfInPool = false;
		    		
		    		// Constantly check if an opponent has joined.
		    		for (Player player : pool) {
			    		if (player.equals(self)) {
			    			selfInPool = true;
			    		} else if (player.getCoin() != self.getCoin()) {
			    			opponent = player;
			    		} else {
			    			removeSelf();
			    			addSelf(observer);
			    			return;
			    		}
			    	}
			    	
		    		// If the player is not in the pool for some reason (likely a server writing mutual exclusion problem),
		    		// then write self to the file.
			    	if (!removedSelf && !selfInPool && pool.size() < 2) {
			    		opponent = null;
			    		file.addLine(PLAYER_POOL_FILE_NAME, self.toString());
			    	}
			    	
		    	} while (opponent == null);

		    	observer.startGame(self, opponent);
		    	
	    		file.removeLines(PLAYER_POOL_FILE_NAME, new String[] {self.toString(), opponent.toString()});
		    }
		};

		listener.start();
	}
	
	/**
	 * Put this thread to sleep while the Player Pool has 2 or more.
	 * @return Content of the file once it contains less than 2 players.
	 */
	private void waitForAvailability() {	
	    int totalWaitTime = 0;
	    
		boolean first = true;
		do {
		    //no need to wait before loading the file for the first time
			if (!first) {
			    if (totalWaitTime < MAX_AVAILABILITY_WAIT_TIME) {
    				try {
    					Thread.sleep(AVAILABILITY_WAIT_TIME);
    					totalWaitTime += AVAILABILITY_WAIT_TIME;
    				} catch (InterruptedException e) {
    					throw new RuntimeException(e);
    				}
			    } 
			    // file has had 2 or more players for maxWaitTime ms. 
			    // those players should have been matched and removed from the pool by now.
			    // there must have been an error in a previous execution. clear the file and continue.
			    else {
			        file.clear(PLAYER_POOL_FILE_NAME);
			        pool.clear();
			        break;
			    }
			} else {
			    first = false;
			}

			reloadPool();
		} while (pool.size() >= 2);
	}	
	
	/**
	 * Reload the player pool from the player info in the file on the server.
	 */
	private void reloadPool() {
		pool.clear();
		
		String content = file.read(PLAYER_POOL_FILE_NAME);
		if (content != null && content.length() > 0)
			for (String line : content.split("\n"))
				if (line != null && line.length() > 0)
					pool.add(new Player(line));
	}
}
