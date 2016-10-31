package frontend;

public interface ViewController {
	public void cleanUp();
	public void disconnect();
	public Canvas getCanvas();
	public ViewID getID(); 
}