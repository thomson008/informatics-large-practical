package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class StatefulDrone extends Drone {
	public List<Station> stationsToVisit = new ArrayList<>();
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
		if (currentTarget == null || isWithinDistance(currentTarget)) {

			
			// Sort the stations in ascending distance order
			currentTarget = Collections.min(stationsToVisit, new Comparator<Station>() {
				public int compare(Station s1, Station s2) {
					if (position.getDistance(s1.coordinates) < position.getDistance(s2.coordinates))
						return -1;
					else if (position.getDistance(s1.coordinates) > position.getDistance(s2.coordinates))
						return 1;
					return 0;
				}
			});
			
			stationsToVisit.remove(currentTarget);
		}
		
		dir = position.computeDirection(currentTarget.coordinates);
		if (!position.nextPosition(dir).inPlayArea()) dir = randomDirection();	

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
	 * <p> Checks if the potential dodge position will improve the situation
	 * @param pos
	 * @return boolean, true if yes, false otherwise
	 */
	private boolean isBetterPosition(Position pos) {
		Station closest = Collections.min(App.stations, new Comparator<Station>() {
			public int compare (Station s1, Station s2) {
				double dist1 = pos.getDistance(s1.coordinates);
				double dist2 = pos.getDistance(s2.coordinates);
				if (dist1 < dist2) 
					return -1;
				else if (dist1 == dist2)
					return 0;
				else 
					return 1;
			}
		});
		
		boolean isWithinNeg = false;
		for (Station s : App.stations) {
			if (pos.getDistance(s.coordinates) <= 0.00025 && (s.getCoins() < 0 || s.getPower() < 0))  {
				isWithinNeg = true;
				break;
			}

		}
		
		return (closest.isPositive() || !isWithinNeg) && pos.inPlayArea();
	}
	
	/**
	 * <p> Changes the direction so it doesn't mindlessly go towards target, but might dodge a negative station if
	 * there's one on its way
	 * @param dir
	 * @return Direction, possibly altered for a dodge
	 */
	private Direction getDodgeDirection(Direction dir) {
		if (!isBetterPosition(position.nextPosition(dir))) {
			int idx = dir.ordinal();
			int newIdx;
			
			for (int i = 0; i < 16; i++) {
				newIdx = (i + idx) % 16;

				Direction testDir = Direction.values()[newIdx];
				if (isBetterPosition(position.nextPosition(testDir)) &&
					position.nextPosition(testDir).getDistance(currentTarget.coordinates) < 
					position.getDistance(currentTarget.coordinates)) 
					dir = testDir;
			}
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
