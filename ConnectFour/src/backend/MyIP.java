package backend;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyIP {
	private static final String hardCodedIP = null; // for testing, leave as null under normal circumstances and remove in final product 
	
	public static String getMyIP() {
		if (hardCodedIP != null)
			return hardCodedIP;
		
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}