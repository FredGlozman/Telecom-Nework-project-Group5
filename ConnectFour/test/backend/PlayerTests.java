import static org.junit.Assert.*;

import org.junit.Test;

import backend.MyIP;
import backend.Player;


public class PlayerTests {

	@Test
	public final void testDefaultConstructor() {
		Player player = new Player();
		assertTrue(player.getHostname().equals(MyIP.getMyIP()));
		assertTrue(player.getCoin() == 0 || player.getCoin() == 1)
	}
	
	@Test
	public final void testStringConstructor() {
		String hostname = "localhost";
		int coin = 0;
		
		Player player = new Player(hostname + "," + coin);
		
		assertTrue(player.getHostname().equals(hostname));
		assertTrue(player.getCoin() == coin);
	}

	@Test
	public final void testHostnameAndCoinValueConstructor() {
		String hostname = "localhost";
		int coin = 0;
		
		Player player = new Player(hostname, coin);
		
		assertTrue(player.getHostname().equals(hostname));
		assertTrue(player.getCoin() == coin);
	}
	
	@Test
	public final void testOpponentConstructor() {
		String hostname = "localhost";
		int coin = 0;
		int expectedPlayerCoinValue = 1;
		
		Player opponent = new Player(hostname, coin);
		Player player = new Player(opponent);
		
		assertTrue(player.getHostname().equals(MyIP.getMyIP()));
		assertTrue(player.getCoin() == expectedPlayerCoinValue);
	}
}
