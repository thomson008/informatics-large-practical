package uk.ac.ed.inf.powergrab;

public abstract class Drone {
	public double power = 250.0;
	public double coins = 0.0;
	public Position position;
	
	public Drone(Position initialPosition) {
		position = initialPosition;
	}
	
	/**
	 * Method that updates the drone position after it makes a move
	 * @param dir Wind direction in which a drone is supposed to move
	 * @return true if the move is permitted, false if it would cause a drone to go outside the play area
	 */
	public boolean makeMove(Direction dir) {
		Position nextPosition =  position.nextPosition(dir);
		
		if(nextPosition.inPlayArea()) {
			position = nextPosition;
			return true;
		}
		
		return false;
	}
	
	/**
	 * Method that handles coins and power exchange between a drone and a station
	 * @param station Station that the drone approaches
	 * @return true if the drone still has power after the exchange, false otherwise
	 */
	public boolean exchangeWithStation(Station station) {
		double stationPower = station.getPower();
		double stationCoins = station.getCoins();
		
		station.setPower(Math.min(0, stationPower + power));
		station.setCoins(Math.min(0, stationCoins + coins));
		
		power = Math.max(0,  power + stationPower);
		coins = Math.max(0, coins + stationCoins);
		
		station.updateSymbol();
		
		return power > 0;
	}
}
