package frontend;

import backend.Player;

public class NetworkConfiguration {
	protected static int scenario; // 0 or 1 - two opponents must be in opposite states
	protected static String ip;
	protected static int port1;
	protected static int port2;
	protected static int userNumber;
	protected static boolean isStarting;
	protected static boolean inSameSubnet;
	
	private static MessageHandler mh;
	
	public static void configNetworking(Player me, Player opponent) {
		inSameSubnet = me.inSameSubnet(opponent);
		
		if (inSameSubnet)
			mh = new SocketMessageHandler();
		else
			mh = new ServerMessageHandler();
		
		String hostname = opponent.getHostname();
		int coin = me.getCoin();
		ip = hostname;
		scenario = coin;
		port1 = 9877 + scenario;
		port2 = 9878 - scenario;
		userNumber = 1 + scenario;
		isStarting = scenario == 0;
	}
	
	public static MessageHandler getMessageHandler() {
		return mh;
	}
}
