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
		int directionIdx;
		Position nextPosition;

		if (posStationWithinMove == null || isWithinDistance(posStationWithinMove)) {
			do {
				directionIdx = random.nextInt(16);
				direction = Direction.values()[directionIdx];
				nextPosition = position.nextPosition(direction);
			} while (!nextPosition.inPlayArea() || nextPosition.inNegativeRange());
		}
		
		else  
			direction = position.computeDirection(posStationWithinMove.coordinates);
		
		
		return direction;
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
