package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StatefulDrone extends Drone {
	private List<Station> stationsToVisit = new ArrayList<>();
	private List<Station> failedToVisit = new ArrayList<>();
	private Station currentTarget;
	private Direction previousDirection;
	private boolean collectedLastStation = false;
	
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
		if (stationsToVisit.isEmpty() && failedToVisit.isEmpty() && currentTarget == getExchangeStation())
			collectedLastStation = true;
		// In case a station was accidentally crossed, remove them from both lists
		stationsToVisit.remove(getExchangeStation());
		failedToVisit.remove(getExchangeStation());
		
		Direction dir = null;
		
		if (stationsToVisit.isEmpty() && failedToVisit.isEmpty() && getExchangeStation() == currentTarget)
			dir = safeDirection();
		
		// If the target has not been set yet or has just been reached
		else if (currentTarget == null || isWithinDistance(currentTarget)) {
			/* If the exchange station is not as expected, add the current target to a list of stations
			 that were failed to collect. The drone will come back to it in the end in order to avoid
			 getting stuck */
			if (currentTarget != null && currentTarget != getExchangeStation()) {
				// Try to go to a place that will allow exchange with that station
				dir = finalDirection(currentTarget);
				failedToVisit.add(currentTarget);	
			}
			
			// Find a new target by looking for the closest positive station
			getNewTarget();
		}
		
		if (dir == null) dir = finalDirection(currentTarget);
		if (dir == null) 
			dir = position.computeDirection(currentTarget.coordinates);
		
		previousDirection = getDodgeDirection(dir);
		return previousDirection;
	}
	
	/**
	 * Acquires new target for the drone
	 */
	private void getNewTarget() {
		if (!stationsToVisit.isEmpty()) {
			currentTarget = Collections.min(stationsToVisit, position.distanceCmp);
			stationsToVisit.remove(currentTarget);
		}
		
		else if (!failedToVisit.isEmpty()) {
			currentTarget = Collections.min(failedToVisit, position.distanceCmp);
			failedToVisit.remove(currentTarget);
		}
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
	 * <p> Checks if the potential dodge position will improve the situation
	 * @param pos
	 * @return boolean, true if yes, false otherwise
	 */
	private boolean isSafePosition(Position pos) {
		Station closest = Collections.min(App.stations, pos.distanceCmp);
		return (closest.isPositive() || !pos.inNegativeRange()) && pos.inPlayArea();
	}
	
	/**
	 * <p> Changes the direction so it doesn't mindlessly go towards target, but might dodge a negative station if
	 * there's one on its way
	 * @param dir Direction it would go to if there wasn't a negative station
	 * @return Direction, possibly altered for a dodge
	 */
	private Direction getDodgeDirection(Direction dir) {
		Direction newDirection = dir;
		
		if (!isSafePosition(position.nextPosition(dir))) {
			int idx = dir.ordinal();
			int newIdx;
			boolean foundSafe = false;
			
			int minDiff = 8;
			
			// Iterate through all directions and find one that dodges a negative station and moves the drone
			// closer to target. If there is more than one, give the one that differs the least from the original
			for (int i = 1; i < 16; i++) {
				newIdx = (i + idx) % 16;
				int diff = directionDiff(newIdx, idx);

				Direction testDir = Direction.values()[newIdx];
				if (isSafePosition(position.nextPosition(testDir))) {
					if (diff < minDiff) {
						newDirection = testDir;
						minDiff = diff;
					}
					
					foundSafe = true;
				}
			}
			
			// If a safe direction was not found, go to the least negative station.
			if (!foundSafe) newDirection = bestNegative(dir);
		}

		// Prevent going backwards, if possible. It might lead to getting stuck
		if (previousDirection != null && !collectedLastStation &&
				newDirection.ordinal() == (previousDirection.ordinal() + 8) % 16) {
				newDirection = preventOpposite(dir.ordinal(), (previousDirection.ordinal() + 8) % 16);
		}
		
		return newDirection;
	}
	
	private int directionDiff(int firstIdx, int secondIdx) {
		int diff = Math.abs(firstIdx - secondIdx);
		if (diff > 8) diff = 16 - diff;
		return diff;
	}
	
	/**
	 * If the drone wants to come back to its previous position in order to dodge an obstacle,
	 * try preventing it from doing so as it might cause it to get stuck
	 * 
	 * @param idx Index of the Direction in which the drone originally goes
	 * @return new Direction, possibly changed so it doesn't go back to where it was
	 */
	private Direction preventOpposite(int idx, int previousIdx) {
		Direction direction;
		Direction clockwise = null;
		Direction cntrClockwise = null;
			
		// Check direction counter-clockwise until one that is safe appears
		for (int i = 1; i < 16; i++) {
			direction = Direction.values()[(idx + i) % 16];
			if (isSafePosition(position.nextPosition(direction))) {
				cntrClockwise = direction;
				break;
			}
		}

		// Repeat the same for clockwise order
		for (int i = 1; i < 16; i++) {
			int index = idx - i;
			if (index < 0) index += 16;
			direction = Direction.values()[index];
			if (isSafePosition(position.nextPosition(direction))) {
				clockwise = direction;
				break;
			}
		}
		
		// Return the direction that differs the least from the original direction
		// and isn't the opposite of the previous direction
		int clkDiff = directionDiff(clockwise.ordinal(), idx);
		int cntrDiff = directionDiff(cntrClockwise.ordinal(), idx);

		if (clockwise.ordinal() == previousIdx) return cntrClockwise;
		else if (cntrClockwise.ordinal() == previousIdx) return clockwise;
		else return clkDiff < cntrDiff ? clockwise : cntrClockwise;
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
			
			if (isStationWithinMove(station) && !station.isPositive() && idxDiff < 4) 
				stations.add(station);	
		}

		Station bestStation = Collections.max(stations, Station.itemsCmp);

		return position.computeDirection(bestStation.coordinates);
	}

	/**
	 * Finds a safe direction for a flight, used when all coins are collected
	 * @return
	 */
	private Direction safeDirection() {
		for (Direction d : Direction.values()) 
			if (isSafePosition(position.nextPosition(d))) return d;
		
		return randomDirection();
	}
}
