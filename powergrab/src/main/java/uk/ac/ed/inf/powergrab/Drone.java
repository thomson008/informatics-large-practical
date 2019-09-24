package uk.ac.ed.inf.powergrab;

public abstract class Drone {
	public double power = 250.0;
	public double coins = 0.0;
	public Position position;
	
	public Drone(Position initialPosition) {
		position = initialPosition;
	}
	
	public boolean makeMove(Direction dir) {
		Position nextPosition =  position.nextPosition(dir);
		
		if(nextPosition.inPlayArea()) {
			position = nextPosition;
			return true;
		}
		
		return false;
	}
	
	public boolean exchangeWithStation(Station station) {
		double stationPower = station.power;
		double stationCoins = station.coins;
		
		station.power = Math.min(0, station.power + power);
		station.coins = Math.min(0, station.coins + coins);
		
		power = Math.max(0,  power + stationPower);
		coins = Math.max(0, coins + stationCoins);
		
		return power > 0;
	}
}
