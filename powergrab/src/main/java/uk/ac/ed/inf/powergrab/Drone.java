package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Tomek
 *
 */
public abstract class Drone {
	public double power = 250.0;
	private double coins = 0.0;
	protected Position position;
	
	/**
	 * getter for coins
	 */

	public double getCoins() {
		return coins;
	}
	
	/**
	 * Checks if the drone still has power left
	 * @return true if it has power, false otherwise
	 */
	public boolean hasPower() {
		return power >= 1.25;
	}
	
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
		power -= 1.25;
	}

	public Direction computeNextMove() {
		return Direction.N;
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
	
	private boolean isWithinDistance(Station station) {
		return (position.getDistance(station.coordinates) <= 0.00025);
	}
	

	
	/**
	 * Gets a station to exchange with, if there is any
	 * @return
	 */
	public Station getExchangeStation() {
		List<Station> stationsWithinRange = new ArrayList<>();
		for (Station station : App.stations) {
			if (isWithinDistance(station))
				stationsWithinRange.add(station);	
		}
		
		if (stationsWithinRange.isEmpty())
			return null;
		
		Station stationWithinRange = Collections.min(stationsWithinRange, new Comparator<Station>() {
			public int compare (Station s1, Station s2) {
				double dist1 = position.getDistance(s1.coordinates);
				double dist2 = position.getDistance(s2.coordinates);
				if (dist1 < dist2) 
					return -1;
				else if (dist1 == dist2)
					return 0;
				else 
					return 1;
			}
		});
		
		return stationWithinRange;
	}
}
