package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class StatelessDrone extends Drone {

	public StatelessDrone(Position initialPosition) {
		super(initialPosition);
	}
	
	/**
	 * Method to find the best direction of the next move for a drone
	 * @return Direction of the best move
	 */
	public Direction computeNextMove() {
		Station stationWithinRange = null;
		Direction direction;
		int directionIdx;
		int i = 0;
		Position nextPosition;
		Random random = new Random();
		
		for (Station station : App.stations) {
			if (position.getDistance(station.coordinates) <= 0.0003)
				stationWithinRange = station;
		}

		do {
			/* if there is no station in range, just go in random direction */
			if (stationWithinRange == null) 
				directionIdx = random.nextInt(16);
			
			/* if a station is found within the range, get the optimal direction for the move*/
			else {
				double angleDirection = this.position.computeAngle(stationWithinRange.coordinates);
				directionIdx = (int) Math.round(angleDirection / 22.5) + i;
			}
			
			i++;
			direction = Direction.values()[directionIdx];
			nextPosition = position.nextPosition(direction);
			
			/* check if the Position after a move in the calculated direction will still be in range */
		} while (!nextPosition.inPlayArea() && i < 16); 

		return direction;
	}
	
	
	
}
