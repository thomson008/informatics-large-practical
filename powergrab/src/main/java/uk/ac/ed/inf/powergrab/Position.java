package uk.ac.ed.inf.powergrab;

/**
 * 
 * @author Tomek
 *
 */
public class Position {
	public double latitude;
	public double longitude;

	/**
	 * Constructor. Initiates the Position object with geographical coordinates
	 * @param latitude Geographical latitude of the point on the map
	 * @param longitude  Geographical longitude of the point on the map
	 */
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Method to compute the new coordinates of the drone after it makes a move
	 * @param direction One of the 16 wind directions
	 * @return Position object representing the position of the drone after one move in the direction passed as an argument
	 */
	public Position nextPosition(Direction direction) {
		int directionIndex = direction.ordinal();
		
		double degreeAngle = directionIndex * 22.5;
		double radianAngle = Math.toRadians(degreeAngle);
		
		double deltaLat = 0.0003 * Math.sin(radianAngle);
		double deltaLong = 0.0003 * Math.cos(radianAngle);
		
		Position newPosition = new Position(latitude + deltaLat, longitude + deltaLong);
		
		return newPosition;
	}
	 /**
	  * Method to check if a move is allowed, i.e. is the drone going to remain within permitted area
	  * @return boolean, true if drone is within the play area, false if it is outside
	  */
	public boolean inPlayArea() { 
		boolean latitudeOK = latitude < 55.946233 && latitude > 55.942617;
		boolean longitudeOK = longitude < -3.184319 && longitude > -3.192473;
		return latitudeOK && longitudeOK;
	}
	
	/**
	 * 
	 * @param position Position of the other point on the map
	 * @return	distance bewteen this position and the position of the other point
	 */
	public double getDistance(Position position) {		
		double xDistance = longitude - position.longitude;
		double yDistance = latitude - position.latitude;
		return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
	}
	
	/**
	 * Computes the angle of the station Position polar coordinates, assuming the Position of a drone is the origin of coordinate system
	 * @param stationPosition position of the station
	 * @return angle in degrees between the drone and the station
	 */
	private double computeAngle(Position stationPosition) {
		double deltaX = stationPosition.longitude - longitude;
		double deltaY = stationPosition.latitude - latitude;
		double angle = Math.atan2(deltaY, deltaX);
		
		if (deltaY < 0) 
			angle += 2 * Math.PI;
		
		return Math.toDegrees(angle);
	}
	
	public Direction computeDirection(Position stationPosition) {
		double angle = computeAngle(stationPosition);
		int directionIdx = ((int) Math.round(angle / 22.5)) % 16;
		Direction direction = Direction.values()[directionIdx];
		return direction;
	}

	/**
	 * Checks if the position is in the range of at least one negative station
	 * @return
	 */
	public boolean inNegativeRange() {
		for (Station s : App.stations) {
			if (!s.isPositive() && getDistance(s.coordinates) <= 0.00025)
				return true;
		}
		return false;
	}
}