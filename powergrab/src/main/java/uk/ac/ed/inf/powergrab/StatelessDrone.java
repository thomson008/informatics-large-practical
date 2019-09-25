package uk.ac.ed.inf.powergrab;

import java.util.Random;

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
		Station stationWithinMove = hasStationWithinMove();
		Station stationWithinRange = hasStationWithinRange();
		Direction direction;
		int directionIdx;
		int i = 0;
		Position nextPosition;
		Random random = new Random();
		
		/* Find stations within one move distance and already in range */

		do {
			/* if there is no station in one move range, or the drone has just paired,
			 * just go in random direction */
			if (stationWithinMove == null || stationWithinRange != null) 
				directionIdx = random.nextInt(16);
			
			/* if a station is found within one move range, get the optimal direction for the move*/
			else {
				double angleDirection = this.position.computeAngle(stationWithinMove.coordinates);
				directionIdx = ((int) Math.round(angleDirection / 22.5) + i) % 16;
				if (stationWithinMove.markerSymbol.equals("danger"))
					directionIdx = (directionIdx + 8) % 16;
			}
			
			i++;
			direction = Direction.values()[directionIdx];
			nextPosition = position.nextPosition(direction);
			
			/* check if the Position after a move in the calculated direction will still be in range */
		} while (!nextPosition.inPlayArea() && i < 16); 

		return direction;
	}
}
