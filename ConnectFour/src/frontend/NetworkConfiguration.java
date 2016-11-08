package frontend;

import backend.Player;

public class NetworkConfiguration {
	protected static int scenario = 0; // 0 or 1 - two opponents must be in opposite states
	
	protected static String ip = "localhost";
	protected static int port1 = 9877 + scenario;
	protected static int port2 = 9878 - scenario;
	protected static int userNumber = 1 + scenario;
	protected static boolean isStarting = scenario == 0;
	protected static boolean inSameSubnet;
	
	public static void configNetworking(Player me, Player opponent) {
		inSameSubnet = me.inSameSubnet(opponent);
		
		String hostname = opponent.getHostname();
		int coin = me.getCoin();
		ip = hostname;
		scenario = coin;
		port1 = 9877 + scenario;
		port2 = 9878 - scenario;
		userNumber = 1 + scenario;
		isStarting = scenario == 0;
	}
}
