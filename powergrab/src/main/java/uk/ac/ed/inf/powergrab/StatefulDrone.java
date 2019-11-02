package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatefulDrone extends Drone {
	
	List<Station> stationsToVisit = new ArrayList<>();
	Station currentTarget;

	public StatefulDrone(Position initialPosition) {
		super(initialPosition);
		
		// Get all the positive stations on the map
		for (Station s : App.stations) {
			if (s.getCoins() > 0 || s.getPower() > 0) {
				stationsToVisit.add(s);
			}
		}
	}
	
	public Direction computeNextMove() {
		if (stationsToVisit.isEmpty() && position.getDistance(currentTarget.coordinates) <= 0.00025) 
			return randomDirection();
		
		Direction dir;
		
		if (currentTarget == null || position.getDistance(currentTarget.coordinates) <= 0.00025) {
			Collections.sort(stationsToVisit, new Comparator<Station>() {
				public int compare(Station s1, Station s2) {
					if (position.getDistance(s1.coordinates) < position.getDistance(s2.coordinates))
						return -1;
					else if (position.getDistance(s1.coordinates) > position.getDistance(s2.coordinates))
						return 1;
					return 0;
				}
			});
			
			int statIdx = 0;
			Station candidate;
			
			do {
				candidate = stationsToVisit.get(statIdx);
				dir = this.position.computeDirection(candidate.coordinates);
				statIdx++;
			} while (!position.nextPosition(dir).inPlayArea());
			
			currentTarget = candidate;
			stationsToVisit.remove(statIdx - 1);
		}
		
		else  
			dir = position.computeDirection(currentTarget.coordinates);
		
		if (isWithinNegative(position.nextPosition(dir))) {
			int idx = dir.ordinal();
			idx = (idx + 2) % 16;
			dir = Direction.values()[idx];
		}
		
		return dir;
	}
	
	private Direction randomDirection() {
		Direction dir;
		
		do {
			dir = Direction.values()[App.random.nextInt(16)];
		} while (!position.nextPosition(dir).inPlayArea());
		
		return dir;
	}	
	
	private boolean isWithinNegative(Position pos) {
		for (Station s : App.stations) {
			if (pos.getDistance(s.coordinates) <= 0.00025 && (s.getCoins() < 0 || s.getPower() < 0)) 
				return true;
		}
		return false;
	}
}
