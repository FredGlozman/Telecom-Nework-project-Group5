package frontend;

import backend.Player;

public class NetworkConfiguration {

	private static MessageHandler mh;
	
	public static void configNetworking(Player me, Player opponent) {
		int coin = me.getCoin();
		
		if (me.inSameSubnet(opponent))
			mh = new SocketMessageHandler(opponent.getHostname(), 9877 + coin, 9878 - coin);
		else
			mh = new ServerMessageHandler();
	}
	
	public static MessageHandler getMessageHandler() {
		return mh;
	}
}
