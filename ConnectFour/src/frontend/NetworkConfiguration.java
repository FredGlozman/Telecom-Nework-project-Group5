package frontend;

import backend.Player;

/**
 * Class that configures the network, i.e. the means to transmit and listen to messages.
 */
public class NetworkConfiguration {
	protected static final int PORT_NUMBER = 9877;

	private static MessageHandler mh;
	
	/**
	 * Configure and initialize the connection; if the players are in the same subnet, set up the socket connection;
	 * otherwise, fall back to the failsafe server mechanism. 
	 * @param me Object representing player.
	 * @param opponent Object representing opponent.
	 */
	public static void configNetworking(Player me, Player opponent) {
		int coin = me.getCoin();
		
		if (me.inSameSubnet(opponent))
			mh = new SocketMessageHandler(opponent.getHostname(), PORT_NUMBER + coin, PORT_NUMBER + 1 - coin);
		else
			mh = new ServerMessageHandler(opponent.getFileName(), me.getFileName());
	}
	
	/**
	 * Getter function for the appropriately set up message handler, depending on the network setup.
	 * @return Message handler.
	 */
	public static MessageHandler getMessageHandler() {
		return mh;
	}
}
