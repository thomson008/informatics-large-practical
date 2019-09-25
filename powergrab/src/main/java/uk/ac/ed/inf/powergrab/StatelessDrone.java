package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class StatelessDrone extends Drone {

	public StatelessDrone(Position initialPosition) {
		super(initialPosition);
	}
	
	public Direction computeNextMove() {
		Station stationWithinRange = null;
		Direction direction;
		
		for (Station station : App.stations) {
			if (position.getDistance(station.coordinates) <= 0.0003)
				stationWithinRange = station;
		}
		
		if (stationWithinRange == null ) {
			Random random = new Random();
			Position nextPosition;
			
			do {
				int idx = random.nextInt();
				direction = Direction.values()[idx];
				nextPosition = position.nextPosition(direction);
			} while (!nextPosition.inPlayArea());
		}
		
		else {
			direction = Direction.N;
		}

		
		
		return direction;
	}
	
	
	
}
