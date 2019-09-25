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
	 */
	public void makeMove(Direction dir) {
		Position nextPosition =  position.nextPosition(dir);
		position = nextPosition;
	}
	
	/**
	 * Method that handles coins and power exchange between a drone and a station
	 * @param station Station that the drone approaches
	 */
	public void exchangeWithStation(Station station) {
		double stationPower = station.getPower();
		double stationCoins = station.getCoins();
		
		station.setPower(Math.min(0, stationPower + power));
		station.setCoins(Math.min(0, stationCoins + coins));
		
		power = Math.max(0,  power + stationPower);
		coins = Math.max(0, coins + stationCoins);
		
		station.updateSymbol();
	}
}
