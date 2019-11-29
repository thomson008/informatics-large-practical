package uk.ac.ed.inf.powergrab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

/**
 * 
 * @author Tomek
 *
 */
public class App {
	public static List<Station> stations;
	private static Drone drone;
	private static String date;
	private static String droneType;
	private static FeatureCollection finalJSON;
	private static String movesLog = "";

    public static void main(String[] args) throws IOException {
    	String jsonURL = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
    	
    	//Get the date as String from input arguments
    	date = args[0] + '-' + args[1] + '-' + args[2];
    	
    	//Get the Json URL
    	Path path = Paths.get(args[2], args[1], args[0], "powergrabmap.geojson");
    	jsonURL += path.toString().replace("\\", "/");
    	
    	//Generate a list of Station objects from JSON
        stations = JSONparser.parseJson(jsonURL);
        
        //Initialise random with the seed given as input argument
        int seed = Integer.parseInt(args[5]);
        
        //Set the initial position of the drone according to input param
        Position initialPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
        
        // Initialise an appropriate type of drone
        droneType = args[6];
        if (droneType.equals("stateless")) 
        	drone = new StatelessDrone(initialPosition, new Random(seed));
        else if (droneType.equals("stateful"))
        	drone = (StatefulDrone) new StatefulDrone(initialPosition, new Random(seed));
        else
        	drone = new StatelessDrone(initialPosition, new Random(seed));	
        
        //Start the game
        playGame();
    }

	private static void playGame() {
		int moves = 0;
		Station stationWithinRange;
		
		//Exchange with a station if there is one in range
		if ((stationWithinRange = drone.getExchangeStation()) != null) 
			drone.exchangeWithStation(stationWithinRange);
		
		//Execute the loop if the conditions are fulfilled
		while (drone.hasPower() && moves < 250) {
			//Compute the direction for the next move
			Direction nextDirection = drone.computeNextMove();
			
			//Get coordinates before making the move
			double latitude = drone.position.latitude;
			double longitude = drone.position.longitude;
			
			//Add the current position to the list of Point objects
			JSONparser.points.add(Point.fromLngLat(longitude, latitude));
			
			drone.makeMove(nextDirection);
			
			//Exchange with a station if there is one in range
			if ((stationWithinRange = drone.getExchangeStation()) != null) 
				drone.exchangeWithStation(stationWithinRange);
			
			//Get new coordinates (after making a move)
			double newLatitude = drone.position.latitude;
			double newLongitude = drone.position.longitude;
			
			//Add the move to the log String
			movesLog += String.format("%f, %f, %s, %f, %f, %.1f, %.2f\n", latitude, longitude, 
					nextDirection.toString(), newLatitude, newLongitude, drone.getCoins(), drone.getPower());

			moves++;
		}
		
		generateFiles();
	}
	
	private static void generateFiles() {
		//Add the last Point to Points list (it won't be added in the loop because the move is done after appending)
		JSONparser.points.add(Point.fromLngLat(drone.position.longitude, drone.position.latitude));
		
		finalJSON = JSONparser.addRouteToFeatures();
		
		String fileName = droneType + '-' + date;
		
		try (PrintWriter out = new PrintWriter(fileName + ".geojson"); 
				PrintWriter out2 = new PrintWriter(fileName + ".txt")) {
		    out.println(finalJSON.toJson());
		    out2.println(movesLog);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
}
