package frontend;

import backend.Player;
import backend.PlayerPool;

public class WaitLogic implements ViewController, PoolObserver {

	private WaitCanvas wc;
	private WindowFrame f;
	
	public WaitLogic(WindowFrame f) {
		this.f = f;
		this.wc = new WaitCanvas(this);
		
		new PlayerPool(this).addSelf();
	}
	
	@Override
	public Canvas getCanvas() {
		return this.wc;
	}
	
	public void startGame(Player me, Player opponent) {				
		NetworkConfiguration.configNetwroking(opponent.getHostname(), me.getCoin());
		this.f.startGame();
	}

	@Override
	public ViewID getID() {
		return ViewID.WaitScreen;
	}
}
