package backend;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Wrapper to get user's IPv4 address.
 */
public class MyIP {
	/**
	 * Returns the IPv4 address of the current user with the mask size (prefix length) in the form a.b.c.d/m
	 * @return The IPv4 address as described above as a string
	 */
	public static String getMyIP() {
		try {
			InetAddress localHost = Inet4Address.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			
			List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
			InterfaceAddress interfaceAddress = null;

			// Find the IPv4 address among the interfaceAddresses using the fact that IPv6 does not have a broadcast address
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