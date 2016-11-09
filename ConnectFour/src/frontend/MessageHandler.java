package frontend;

public interface MessageHandler {
	public void sendMessage(MiddleWare mw, int message);
	public void listen(MiddleWare mw);
	public void close();
}
