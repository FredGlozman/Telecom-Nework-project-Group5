package frontend;

public class NetworkConfiguration {
	protected static int CASE = 0; // 0 or 1 - two opponents must be in opposite states
	
	protected static String IP = "localhost";
	protected static int PORT_1 = 9877 + CASE;
	protected static int PORT_2 = 9878 - CASE;
	protected static int USER_NUMBER = 1 + CASE;
	protected static boolean START = CASE == 0;
	
	public static void configNetwroking(String hostname, int coin) {
		IP = hostname;
		CASE = coin;
		PORT_1 = 9877 + CASE;
		PORT_2 = 9878 - CASE;
		USER_NUMBER = 1 + CASE;
		START = CASE == 0;
	}
}
