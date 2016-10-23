package frontend;

public class WaitLogic implements ViewController {

	private WaitCanvas wc;
	private WindowFrame f;
	
	public WaitLogic(WindowFrame f) {
		this.f = f;
		this.wc = new WaitCanvas(this);
	}
	
	@Override
	public Canvas getCanvas() {
		return this.wc;
	}
	
	public void startGame() {
		this.f.startGame();
	}

}
