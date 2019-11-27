package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class StatefulDrone extends Drone {
	private List<Station> stationsToVisit = new ArrayList<>();
	private Station currentTarget;

	public StatefulDrone(Position initialPosition, Random random) {
		super(initialPosition, random);
		
		// Get all the positive stations on the map
		for (Station s : App.stations) {
			if (s.getCoins() > 0 || s.getPower() > 0) {
				stationsToVisit.add(s);
			}
		}
	}
	
	public Direction computeNextMove() {
		if (stationsToVisit.isEmpty() && position.getDistance(currentTarget.coordinates) <= 0.00025)
			return safeDirection();
		
		Direction dir;
		
		// If the target has not been set yet or has just been reached
		if (currentTarget == null || position.getDistance(currentTarget.coordinates) <= 0.00025) {
			
			// Sort the stations in ascending distance order
			Collections.sort(stationsToVisit, new Comparator<Station>() {
				public int compare(Station s1, Station s2) {
					if (position.getDistance(s1.coordinates) < position.getDistance(s2.coordinates))
						return -1;
					else if (position.getDistance(s1.coordinates) > position.getDistance(s2.coordinates))
						return 1;
					return 0;
				}
			});
			
			currentTarget = stationsToVisit.get(0);
			stationsToVisit.remove(0);
			dir = position.computeDirection(currentTarget.coordinates);
		}
		
		// Else, so if the target is currently set, go in the direction of it
		else  {
			dir = position.computeDirection(currentTarget.coordinates);
			
			// If that direction is out of range, go randomly
			if (!position.nextPosition(dir).inPlayArea()) dir = randomDirection();	
		}

		return getDodgeDirection(dir);
	}
	
	/**
	 * Generates random direction
	 * @return Direction, random
	 */
	private Direction randomDirection() {
		Direction dir;
		
		do {
			dir = Direction.values()[random.nextInt(16)];
		} while (!position.nextPosition(dir).inPlayArea());
		
		return dir;
	}	
	
	/**
	 * <p> Checks if there is a negative station within charging distance
	 * @param pos
	 * @return boolean, true if there is such station, false otherwise
	 */
	private boolean isWithinNegative(Position pos) {
		for (Station s : App.stations) {
			if (pos.getDistance(s.coordinates) <= 0.00025 && (s.getCoins() < 0 || s.getPower() < 0)) 
				return true;
		}
		return false;
	}
	
	/**
	 * <p> Checks if there is a negative station within charging distance
	 * @param pos
	 * @return boolean, true if there is such station, false otherwise
	 */
	private boolean isWithinPositive(Position pos) {
		for (Station s : App.stations) {
			if (pos.getDistance(s.coordinates) <= 0.00025 && s.getCoins() >= 0 && s.getPower() >= 0)
				return true;
		}
		return false;
	}
	
	/**
	 * <p> Changes the direction so it doesn't mindlessly go towards target, but might dodge a negative station if
	 * there's one on its way
	 * @param dir
	 * @return Direction, possibly altered for a dodge
	 */
	private Direction getDodgeDirection(Direction dir) {
		if (isWithinNegative(position.nextPosition(dir)) && !isWithinPositive(position.nextPosition(dir))) {
			int idx = dir.ordinal();
			
			do {
				idx = (idx + 1) % 16;
			    dir = Direction.values()[idx];
			} while (!position.nextPosition(dir).inPlayArea() || isWithinNegative(position.nextPosition(dir)));
		}
		
		return dir;
	}
	
	private Direction safeDirection() {
		for (Direction d : Direction.values()) {
			if (!isWithinNegative(position.nextPosition(d)) && position.nextPosition(d).inPlayArea()) {
				return d;
			}
		}
		
		return null;
	}
}
