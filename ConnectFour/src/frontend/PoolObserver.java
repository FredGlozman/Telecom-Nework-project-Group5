package frontend;

import backend.Player;

/**
 * Interface for classes that must launch the game.
 */
public interface PoolObserver {
	
	/**
	 * Signal to begin the game.
	 * @param me Player's information.
	 * @param opponent Opponent's information.
	 */
	public void startGame(Player me, Player opponent);
}
