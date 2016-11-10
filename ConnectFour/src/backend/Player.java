package backend;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Player {
    private final String fileName;
	private final String hostname;
	private final short mask;
	private final int coin; //0 or 1
	
	/**
	 * Determines this player's IP and infers this player's coin value based on the coin value of the oponent
	 * @param opponent this player's opponent
	 */
	public Player(Player opponent) {
		String fullIPAsString = MyIP.getMyIP();
		String[] fullIP = MyIP.getMyIP().split("/");
		
		if(fullIP.length != 2) {
			throw new RuntimeException("Error, the IP: [" + fullIPAsString + "] has an unexpected format");
		}
		
		this.hostname = fullIP[0];
		this.mask = Short.parseShort(fullIP[1]);
		this.coin = inferCoin(opponent.getCoin());
		this.fileName = generateFileName(this.hostname);
	}
	
	/**
	 * @param hostname Player's IP
	 * @param coin player's coin value (either 0 or 1)
	 */
	public Player(String hostname, short mask, int coin) {
		this.hostname = hostname;
		this.mask = mask;
		this.coin = coin;
		this.fileName = generateFileName(this.hostname);
	}
	
	/**
	 * Constructs the player object based on player info as written in the text file on the server.
	 * @param playerInfo format:hostname,coinValue
	 */
	public Player(String playerInfo) {		
		String[] components = playerInfo.split(",");
		
		if(components.length != 3) {
			throw new RuntimeException("Error, the player info: [" + playerInfo + "] has an unexpected format");
		}
	
		String[] fullIP = components[0].split("/");
		
		if(fullIP.length != 2) {
			throw new RuntimeException("Error, the IP: [" + components[0] + "] has an unexpected format");
		}
		
		this.hostname = fullIP[0];
		this.mask = Short.parseShort(fullIP[1]);
		this.coin = Integer.parseInt(components[1]);
		this.fileName = components[2];
	}
		
	/**
	 * Default constructor. Determines the players IP
	 * and randomly assigns the player a coin value (0 or 1).
	 */
	public Player() {
		String fullIPAsString = MyIP.getMyIP();
		String[] fullIP = MyIP.getMyIP().split("/");
		
		if(fullIP.length != 2) {
			throw new RuntimeException("Error, the IP: [" + fullIPAsString + "] has an unexpected format");
		}
		
		this.hostname = fullIP[0];
		this.mask = Short.parseShort(fullIP[1]);
		this.coin = coinFlip();
		this.fileName = generateFileName(this.hostname);
	}
	
	/**
	 * Checks whether two players are in the same subnet
	 * @param other	Player to be compared with
	 * @return		Whether both players are in the same subnet
	 */
	public boolean inSameSubnet(Player other) {
		short mask = other.getMask();
		if (this.mask < mask)
			mask = this.mask;
		
		long thisIP = ipAsInt(this.hostname);
		long otherIP = ipAsInt(other.getHostname());
		
		return getUnmaskedValue(thisIP, mask) == getUnmaskedValue(otherIP, mask);
	}
	
	/**
	 * Gets the ip as a long integer	
	 * @param hostname IP as string
	 * @return IP as long integer
	 */
	private long ipAsInt(String hostname) {
		byte[] ipAsByteArray;
		
		try {
			ipAsByteArray = InetAddress.getByName(hostname).getAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
		
		long ipAsInt = 0;
		for (byte b : ipAsByteArray) {
			ipAsInt <<= 8;
			ipAsInt += Byte.toUnsignedInt(b);
		}
		
		return ipAsInt;
	}
	
	/**
	 * Return only the unmasked part of the IP address
	 * @param ip IP as long integer
	 * @param mask Masked part of the IP
	 * @return Unmasked part of the IP as long integer
	 */
	private long getUnmaskedValue(long ip, short mask) {
		long unmaskedPart = 0;
		for (short m = 0; m < 32 - mask; m++) {
			unmaskedPart <<= 1;
			unmaskedPart += 1;
		}
		return ip & unmaskedPart;
	}
	
	/**
	 * @return hostname of this player
	 */
	public String getHostname() {
		return this.hostname;
	}
	
	/**
	 * @return mask of this player's IP
	 */
	public short getMask() {
		return this.mask;
	}
	
	/**
	 * @return coin value of this player (either 0 or 1)
	 */
	public int getCoin() {
		return this.coin;
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * Used for storing player information on server file.
	 * format:hostname/mask,coinValue
	 */
	public String toString() {
		return hostname + "/" + mask + "," + coin + "," + fileName;
	}
	
	/**
	 * If the opponent's coin is 1, this player's coin must be 0. 
	 * Conversely, if the opponent's coin is 0, this player's coin must be 1.
	 * @param opponentCoin the coin belonging to the opponent
	 * @return this player's coin
	 */
	private int inferCoin(int opponentCoin) {
		int myCoin = -1;
		if(opponentCoin == 0) {
			myCoin = 1;
		} else if(opponentCoin == 1){
			myCoin = 0;
		} else {
			throw new RuntimeException("Error, the opponent's coin cannot be: " + opponentCoin);
		}
		
		return myCoin;
	}
	
	/**
	 * Generate 0 or 1 randomly
	 * @return returns 0 or 1 randomly
	 */
	private int coinFlip() {
		double random = Math.random();
		if (random < 0.5) {
			return 0;
		} else {
			return 1;
		}
	}
	
	private String generateFileName(String IP) {
	    String fileName = IP.replace(".", "-");
	    int rand = (int) (Math.random() * 100000);
	    return fileName + "-" + rand + ".txt";
	}
}
