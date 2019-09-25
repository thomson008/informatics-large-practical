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
		Station stationWithinMove = null;
		Station stationWithinDistance = null;
		Direction direction;
		int directionIdx;
		int i = 0;
		Position nextPosition;
		Random random = new Random();
		
		for (Station station : App.stations) {
			if (position.getDistance(station.coordinates) <= 0.0003 || isWithinDistance(station))
				stationWithinMove = station;
				if (isWithinDistance(station)) 
					stationWithinDistance = station;	
		}

		do {
			/* if there is no station in Move, just go in random direction */
			if (stationWithinMove == null || stationWithinDistance != null) 
				directionIdx = random.nextInt(16);
			
			/* if a station is found within the Move, get the optimal direction for the move*/
			else {
				double angleDirection = this.position.computeAngle(stationWithinMove.coordinates);
				directionIdx = ((int) Math.round(angleDirection / 22.5) + i) % 16;
				if (stationWithinMove.markerSymbol.equals("danger"))
					directionIdx = (directionIdx + 8) % 16;
			}
			
			i++;
			direction = Direction.values()[directionIdx];
			nextPosition = position.nextPosition(direction);
			
			/* check if the Position after a move in the calculated direction will still be in Move */
		} while (!nextPosition.inPlayArea() && i < 16); 

		return direction;
	}
}
