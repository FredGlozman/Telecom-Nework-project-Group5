package backend;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

public class MyIP {
	private static final String hardCodedIP = null; // for testing, leave as null under normal circumstances and remove in final product 
	
	public static String getMyIP() {
		if (hardCodedIP != null)
			return hardCodedIP;
		
		try {
			InetAddress localHost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			
			List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
			InterfaceAddress interfaceAddress = null;
			
            for (InterfaceAddress ia : interfaceAddresses) {
                if (ia.getBroadcast() != null) {
                    interfaceAddress = ia;
                    break;
                }
            }

            if (interfaceAddress == null) {
                throw new RuntimeException("Error, no IPv4 address found.");
            }
			
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