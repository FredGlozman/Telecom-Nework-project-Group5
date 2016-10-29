package backend;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MyIP {
	public static String getMyIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
}