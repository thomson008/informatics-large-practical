package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
		Station negStationWithinMove = getNegStationWithinMove();
		Direction direction;
		int directionIdx;
		Position nextPosition;

		if (posStationWithinMove == null && negStationWithinMove == null) {
			do {
				directionIdx = random.nextInt(16);
				direction = Direction.values()[directionIdx];
				nextPosition = position.nextPosition(direction);
			} while (!nextPosition.inPlayArea());
		}
		
		else if (posStationWithinMove != null) {
			direction = position.computeDirection(posStationWithinMove.coordinates);
		}
		
		else {
			int i = 0;
			do {
				Direction oppositeDirection = position.computeDirection(negStationWithinMove.coordinates);
				directionIdx = (Arrays.asList(Direction.values()).indexOf(oppositeDirection) + 8 + i) % 16;
				direction =  Direction.values()[directionIdx];
				nextPosition = position.nextPosition(direction);
				i++;
			} while (!nextPosition.inPlayArea());

		}

		return direction;
	}
	
	/**
	 * Gets the list of all positively charged stations within a range of one move
	 * Also checks if moving in that direction wouldn't cause the drone to move outside the play area
	 * @return
	 */
	public Station getPosStationWithinMove() {
		List<Station> stations = new ArrayList<>();
		
		for (Station station : App.stations) {
			double distance = position.getDistance(station.coordinates);
			Direction dir = position.computeDirection(station.coordinates);
			Position hypotheticalNextPos = position.nextPosition(dir);
			if (distance <= 0.00055 && station.getCoins() > 0 && hypotheticalNextPos.inPlayArea())
				stations.add(station);	
		}
		
		if (stations.isEmpty())
			return null;
		
		Station bestStation = Collections.max(stations, new Comparator<Station>() {
			public int compare(Station s1, Station s2) {
				if (s1.getCoins() < s2.getCoins())
					return -1;
				else if (s1.getCoins() == s2.getCoins())
					return 0;
				else 
					return -1;
			}
		});
		
		return bestStation;
	}
	
	/**
	 * Gets the list of all negatively charged stations within a range of one move
	 * @return
	 */
	public Station getNegStationWithinMove() {
		List<Station> stations = new ArrayList<>();
		
		for (Station station : App.stations) {
			if (position.getDistance(station.coordinates) <= 0.00055 && !station.isPositive())
				stations.add(station);	
		}
		
		if (stations.isEmpty())
			return null;
		
		Station worstStation = Collections.min(stations, new Comparator<Station>() {
			public int compare(Station s1, Station s2) {
				if (s1.getCoins() < s2.getCoins())
					return -1;
				else if (s1.getCoins() == s2.getCoins())
					return 0;
				else 
					return -1;
			}
		});
		
		return worstStation;
	}
}
