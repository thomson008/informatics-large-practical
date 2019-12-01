package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class Drone {
	private double power = 250.0;
	private double coins = 0.0;
	protected Position position;
	protected Random random;
	protected static Comparator<Station> itemsCmp = new Comparator<Station>() {
		public int compare(Station s1, Station s2) {
			if (s1.getCoins() + s1.getPower() < s2.getCoins() + s2.getPower())
				return -1;
			else if (s1.getCoins() + s1.getPower() == s2.getCoins() + s2.getPower())
				return 0;
			else 
				return 1;
		}
	};
	
	/**
	 * getter for coins
	 */

	public double getCoins() {
		return coins;
	}
	
	public double getPower() {
		return power;
	}
	
	/**
	 * Checks if the drone still has power left
	 * @return true if it has power, false otherwise
	 */
	public boolean hasPower() {
		return power >= 1.25;
	}
	
	public Drone(Position initialPosition, Random random) {
		position = initialPosition;
		this.random = random;
	}
	
	/**
	 * Method that updates the drone position after it makes a move
	 * @param dir Wind direction in which a drone is supposed to move
	 */
	public void makeMove(Direction dir) {
		position =  position.nextPosition(dir);
		power -= 1.25;
	}

	/**
	 * Default method, in fact is never used because its overridden by Stateless or Stateful
	 * @return
	 */
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
	}
	
	protected boolean isWithinDistance(Station station) {
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
		
		Station stationWithinRange = Collections.min(stationsWithinRange, position.distanceCmp);
		
		return stationWithinRange;
	}
	
	/**
	 * Used when a drone is within one move of a positive station, to find the first
	 * direction that will make it exchange with that station (i.e. make it its closest station)
	 * @param target
	 * @return
	 */
	protected Direction finalDirection(Station target) {
		for (int i = 0; i < 16; i++) {
			Direction dir = Direction.values()[i];
			Position next = position.nextPosition(dir);
			if (next.getClosest() == target && next.getDistance(target.coordinates) <= 0.00025 
				&& next.inPlayArea())
				return dir;
		}
		return null;
	}
}
