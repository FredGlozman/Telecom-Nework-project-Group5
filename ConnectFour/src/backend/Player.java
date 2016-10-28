package backend;

public class Player {
	private final String hostname;
	private final int coin; //0 or 1
	
	/**
	 * Determines this player's IP and infers this player's coin value based on the coin value of the oponent
	 * @param opponent this player's oponent
	 */
	public Player(Player opponent) {
		this.hostname = MyIP.getMyIP();
		this.coin = inferCoin(opponent.getCoin());
	}
	
	/**
	 * @param hostname Player's IP
	 * @param coin player's coin value (either 0 or 1)
	 */
	public Player(String hostname, int coin) {
		this.hostname = hostname;
		this.coin = coin;
	}
	
	/**
	 * Constructs the player object based on player info as written in the text file on the server.
	 * @param playerInfo format:hostname,coinValue
	 */
	public Player(String playerInfo) {		
		String[] components = playerInfo.split(",");
		
		if(components.length != 2) {
			throw new RuntimeException("Error, the player info: [" + playerInfo + "] has an unexpected format");
		}
	
		this.hostname = components[0];
		this.coin = Integer.parseInt(components[1]);
	}
		
	/**
	 * Default constructor. Determines the players IP
	 * and randomly assigns the player a coin value (0 or 1).
	 */
	public Player() {
		this.hostname = MyIP.getMyIP();
		this.coin = coinFlip();
	}
	
	/**
	 * @return hostname of this player
	 */
	public String getHostname() {
		return this.hostname;
	}
	
	/**
	 * @return coin value of this player (either 0 or 1)
	 */
	public int getCoin() {
		return this.coin;
	}
	
	/**
	 * Used for storing player information on server file.
	 * format:hostname,coinValue
	 */
	public String toString() {
		return hostname + "," + coin;
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
		if(random<0.5) {
			return 0;
		} else {
			return 1;
		}
	}
}
