package uk.ac.ed.inf.powergrab;

import java.util.Arrays;
import java.util.List;


public class StatelessDrone extends Drone {
	public StatelessDrone(Position initialPosition) {
		super(initialPosition);
		this.position = initialPosition;
	}
	
	/**
	 * Method to find the best direction of the next move for a drone
	 * @return Direction of the best move
	 */
	public Direction computeNextMove() {
		List<Station> posStationsWithinMove = getPosStationsWithinMove();
		List<Station> negStationsWithinMove = getNegStationsWithinMove();
		Station stationWithinRange = getExchangeStation();
		Direction direction;
		int directionIdx;
		Position nextPosition;

		if (posStationsWithinMove.isEmpty() && negStationsWithinMove.isEmpty() || stationWithinRange != null) {
			do {
				directionIdx = App.random.nextInt(16);
				direction = Direction.values()[directionIdx];
				nextPosition = position.nextPosition(direction);
			} while (!nextPosition.inPlayArea());
		}
		
		else if (!posStationsWithinMove.isEmpty()) {
			direction = position.computeDirection(posStationsWithinMove.get(0).coordinates);
		}
		
		else {
			Direction oppositeDirection = position.computeDirection(negStationsWithinMove.get(0).coordinates);
			directionIdx = (Arrays.asList(Direction.values()).indexOf(oppositeDirection) + 8) % 16;
			direction =  Direction.values()[directionIdx];
		}

		return direction;
	}
}
