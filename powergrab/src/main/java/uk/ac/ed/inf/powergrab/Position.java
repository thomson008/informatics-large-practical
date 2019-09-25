package uk.ac.ed.inf.powergrab;

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
		
		Position newPosition = new Position(this.latitude + deltaLat, this.longitude + deltaLong);
		
		return newPosition;
	}
	 /**
	  * Method to check if a move is allowed, i.e. is the drone going to remain within permitted area
	  * @return boolean, true if drone is within the play area, false if it is outside
	  */
	public boolean inPlayArea() { 
		boolean latitudeOK = this.latitude < 55.946233 && this.latitude > 55.942617;
		boolean longitudeOK = this.longitude < -3.184319 && this.latitude > -3.192473;
		return latitudeOK && longitudeOK;
	}
	
	/**
	 * 
	 * @param position Position of the other point on the map
	 * @return	distance bewteen this position and the position of the other point
	 */
	public double getDistance(Position position) {		
		double xDistance = this.longitude - position.longitude;
		double yDistance = this.latitude - position.latitude;
		return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
	}
	
	/**
	 * Computes the angle of the station Position polar coordinates, assuming the Position of a drone is the origin of coordinate system
	 * @param stationPosition position of the station
	 * @return angle in degrees between the drone and the station
	 */
	public double computeAngle(Position stationPosition) {
		double deltaX = stationPosition.longitude - longitude;
		double deltaY = stationPosition.latitude - latitude;
		double angle = Math.atan2(deltaY, deltaX);
		
		if (deltaY < 0) 
			angle += 2 * Math.PI;
		
		return Math.toDegrees(angle);
	}
}