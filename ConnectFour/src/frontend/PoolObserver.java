package frontend;

import backend.Player;

public interface PoolObserver {
	/**
	 * Signals to begin the game.
	 * @param me your player info.
	 * @param opponent opponent's player info.
	 */
	public void startGame(Player me, Player opponent);
}
