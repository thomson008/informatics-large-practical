package uk.ac.ed.inf.powergrab;

import java.util.Arrays;

/**
 * 
 * @author Tomek
 *
 */
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
		Station posStationWithinMove = getPosStationWithinMove();
		Station negStationWithinMove = getNegStationWithinMove();
		Direction direction;
		int directionIdx;
		Position nextPosition;

		if (posStationWithinMove == null && negStationWithinMove == null) {
			do {
				directionIdx = App.random.nextInt(16);
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
}
