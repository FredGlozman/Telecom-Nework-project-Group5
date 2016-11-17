package frontend;

/**
 * Wrapper to handle front-end components; abstracts away the front-end from the back-end.
 */
public interface MiddleWare {

	/**
	 * Inform the front-end that a message has been received.
	 * @param data Message received.
	 */
	public void transferData(int data);
	
	/**
	 * Inform the front-end that the message it tried to transmit failed to be delivered.
	 */
	public void transferFail();
	
}
