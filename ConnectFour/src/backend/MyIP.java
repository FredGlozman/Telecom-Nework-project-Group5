package backend;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MyIP {
	private static final String hardCodedIP = null; // for testing, leave as null under normal circumstances and remove in final product 
	
	public static String getMyIP() {
		if (hardCodedIP != null)
			return hardCodedIP;
		
		try {
			InetAddress localHost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			InterfaceAddress interfaceAddress = networkInterface.getInterfaceAddresses().get(0);
			String ip = interfaceAddress.getAddress().getHostAddress();
			short mask = interfaceAddress.getNetworkPrefixLength();
			return ip + "/" + mask;
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}
}