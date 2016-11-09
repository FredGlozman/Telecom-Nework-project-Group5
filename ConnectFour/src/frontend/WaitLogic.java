package frontend;

import backend.Player;
import backend.PlayerPool;

public class WaitLogic implements ViewController, PoolObserver {

	private WaitCanvas wc;
	private WindowFrame f;
	
	public WaitLogic(WindowFrame f) {
		this.f = f;
		this.wc = new WaitCanvas(this);
		
		PlayerPool.getInstance().addSelf(this);
	}
	
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
