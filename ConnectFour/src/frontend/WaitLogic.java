package frontend;

import backend.Player;
import backend.PlayerPool;

/**
 * Wait controller: Responsible for placing the player in queue and launching the game when another player is found.
 */
public class WaitLogic implements ViewController, PoolObserver {

	private WaitCanvas wc;
	private WindowFrame f;
	
	/**
	 * Constructor: places player in queue using the tools provided by PlayerPool.
	 * @param f Pointer to the frame containing the view.
	 */
	public WaitLogic(WindowFrame f) {
		this.f = f;
		this.wc = new WaitCanvas(this);
		
		PlayerPool.getInstance().addSelf(this);
	}
	
	/**
	 * Launch the game by configuring the network (i.e. establishing appropriate connections) and updating the view.
	 */
	public void startGame(Player me, Player opponent) {				
		NetworkConfiguration.configNetworking(me, opponent);
		this.f.startGame(me, opponent);
	}
	
	@Override
	public Canvas getCanvas() {
		return this.wc;
	}

	@Override
	public ViewID getID() {
		return ViewID.WAIT;
	}

	@Override
	public void cleanUp() {
		PlayerPool.getInstance().removeSelf();
	}

	@Override
	public void disconnect() {}
}
