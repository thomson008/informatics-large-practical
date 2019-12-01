package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 
 * @author Tomek
 *
 */
public class StatelessDrone extends Drone {
	public StatelessDrone(Position initialPosition, Random random) {
		super(initialPosition, random);
	}
	
	/**
	 * Method to find the best direction of the next move for a drone
	 * @return Direction of the best move
	 */
	public Direction computeNextMove() {
		Station posStationWithinMove = getPosStationWithinMove();
		Direction direction;


		if (posStationWithinMove == null || isWithinDistance(posStationWithinMove)) 			
			direction = generateRandomDirection();
		
		else  {
			direction = finalDirection(posStationWithinMove);
			if (direction == null)
				direction = position.computeDirection(posStationWithinMove.coordinates);
			if (!position.nextPosition(direction).getClosest().isPositive())
				direction = generateRandomDirection();
		}

		return direction;
	}
	
	/**
	 * Generates random direction that will not put the drone in a exchange range with a negative station
	 * (if that is possible)
	 * @return
	 */
	private Direction generateRandomDirection() {
		Position nextPosition;
		int directionIdx;
		Direction direction;
		
		do {
			directionIdx = random.nextInt(16);
			direction = Direction.values()[directionIdx];
			nextPosition = position.nextPosition(direction);
			// Continue looking for directions that will not put the drone in negative area,
			// unless there is no such direction
		} while (!nextPosition.inPlayArea() || nextPosition.inNegativeRange() && !isSurrounded());
		
		return direction;
	}

	/**
	 * Checks if there is any move that will not put the drone in a negative area
	 * @return
	 */
	private boolean isSurrounded() {
		for (Direction dir : Direction.values()) {
			if (!position.nextPosition(dir).inNegativeRange() && position.nextPosition(dir).inPlayArea())
				return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the list of all positively charged stations within a range of one move
	 * Also checks if moving in that direction wouldn't cause the drone to move outside the play area
	 * @return
	 */
	private Station getPosStationWithinMove() {
		List<Station> stations = new ArrayList<>();
		
		for (Station station : App.stations) {
			double distance = position.getDistance(station.coordinates);
			Direction dir = position.computeDirection(station.coordinates);
			Position hypotheticalNextPos = position.nextPosition(dir);
			if (distance <= 0.00055 && (station.getCoins() > 0 || station.getPower() > 0) && hypotheticalNextPos.inPlayArea())
				stations.add(station);	
		}
		
		if (stations.isEmpty())
			return null;
		
		Station bestStation = Collections.max(stations, itemsCmp);
		
		return bestStation;
	}
}
