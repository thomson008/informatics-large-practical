package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class StatefulDrone extends Drone {
	private List<Station> stationsToVisit = new ArrayList<>();
	private List<Station> failedToVisit = new ArrayList<>();
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
		if (stationsToVisit.isEmpty() && failedToVisit.isEmpty() && position.getDistance(currentTarget.coordinates) <= 0.00025)
			return safeDirection();
		
		Direction dir;
		
		// If the target has not been set yet or has just been reached
		if (currentTarget == null || isWithinDistance(currentTarget)) {
			/* If the exchange station is not as expected, add the current target to a list of stations
			 that were failed to collect. The drone will come back to it in the end in order to avoid
			 getting stuck */
			if (currentTarget != null && currentTarget != getExchangeStation())
				failedToVisit.add(currentTarget);
			
			Comparator<Station> stationCmp = new Comparator<Station>() {
				public int compare(Station s1, Station s2) {
					if (position.getDistance(s1.coordinates) < position.getDistance(s2.coordinates))
						return -1;
					else if (position.getDistance(s1.coordinates) > position.getDistance(s2.coordinates))
						return 1;
					return 0;
				}
			};
			
			// Find a new target by looking for the closest positive station
			if (!stationsToVisit.isEmpty()) {
				currentTarget = Collections.min(stationsToVisit, stationCmp);
				stationsToVisit.remove(currentTarget);
			}
			else {
				currentTarget = Collections.min(failedToVisit, stationCmp);
				failedToVisit.remove(currentTarget);
			}
		}
		
		stationsToVisit.remove(getExchangeStation());
		dir = position.computeDirection(currentTarget.coordinates);

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
	private boolean isSafePosition(Position pos) {
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
		if (!isSafePosition(position.nextPosition(dir))) {
			int idx = dir.ordinal();
			int newIdx;
			boolean foundSafe = false;
			
			for (int i = 0; i < 16; i++) {
				newIdx = (i + idx) % 16;

				Direction testDir = Direction.values()[newIdx];
				if (isSafePosition(position.nextPosition(testDir)) &&
					position.nextPosition(testDir).getDistance(currentTarget.coordinates) < 
					position.getDistance(currentTarget.coordinates)) {
					dir = testDir;
					foundSafe = true;
				}
			}
			
			// If a safe direction was not found, go to the least negative station.
			if (!foundSafe) dir = bestNegative(dir);
		}
		
		return dir;
	}
	
	/**
	 * Gives the direction of the least negative station, in case there is no way to avoid a negative one
	 * @return
	 */
	private Direction bestNegative(Direction dir) {
		List<Station> stations = new ArrayList<>();

		for (Station station : App.stations) {
			int dirIdx = dir.ordinal();
			int newDirIdx = position.computeDirection(station.coordinates).ordinal();
			
			int idxDiff = Math.abs(dirIdx - newDirIdx);
			if (idxDiff > 8) idxDiff = 16 - idxDiff;
			
			if (position.getDistance(station.coordinates) <= 0.00055 && !station.isPositive() && idxDiff < 4) 
				stations.add(station);	
		}

		Station bestStation = Collections.max(stations, new Comparator<Station>() {
			public int compare(Station s1, Station s2) {
				if (s1.getCoins() + s1.getPower() < s2.getCoins() + s2.getPower())
					return -1;
				else if (s1.getCoins() == s2.getCoins())
					return 0;
				else 
					return 1;
			}
		});

		return position.computeDirection(bestStation.coordinates);
	}

	/**
	 * Finds a safe direction for a flight, used when all coins are collected
	 * @return
	 */
	private Direction safeDirection() {
		for (Direction d : Direction.values()) {
			if (!isWithinNegative(position.nextPosition(d)) && position.nextPosition(d).inPlayArea()) {
				return d;
			}
		}
		return getDodgeDirection(randomDirection());
	}
}
