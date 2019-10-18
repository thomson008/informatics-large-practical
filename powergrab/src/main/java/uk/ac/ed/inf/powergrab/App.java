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
 * @author th
 *
 */
public class App {
	public static Random random;
	public static List<Station> stations;
	public static Drone drone;
	public static String date;
	public static String droneType;
	
	public static String outputJson;
	public static FeatureCollection finalJSON;
	public static String movesLog = "";


	
    public static void main(String[] args) throws IOException {
    	String jsonURL = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
    	date = args[0] + '-' + args[1] + '-' + args[2];
    	Path path = Paths.get(args[2], args[1], args[0], "powergrabmap.geojson");
    	jsonURL += path.toString().replace("\\", "/");
        stations = JSONparser.parseJson(jsonURL);
        random = new Random(Integer.parseInt(args[5]));
        
        Position initialPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
        
        droneType = args[6];
        if (droneType.equals("stateless")) 
        	drone = new StatelessDrone(initialPosition);
        else if (droneType.equals("stateful"))
        	drone = (StatefulDrone) new StatefulDrone(initialPosition);
        else
        	drone = new StatelessDrone(initialPosition);	
        
        playGame();
    }

	private static void playGame() {
		int moves = 0;
		Station stationWithinRange;
		
		while (drone.hasPower() && moves < 250) {
			if ((stationWithinRange = drone.getExchangeStation()) != null) 
				drone.exchangeWithStation(stationWithinRange);
			
			Direction nextDirection = drone.computeNextMove();
			
			double latitude = drone.position.latitude;
			double longitude = drone.position.longitude;
			
			JSONparser.points.add(Point.fromLngLat(longitude, latitude));
			
			drone.makeMove(nextDirection);
			
			double newLatitude = drone.position.latitude;
			double newLongitude = drone.position.longitude;
			
			movesLog += String.format("%f, %f, %s, %f, %f, %f, %f\n", latitude, longitude, 
					nextDirection.toString(), newLatitude, newLongitude, drone.getCoins(), drone.power);

			moves++;
		}
		
		generateFiles();
		

		
		System.out.println(movesLog);
	}
	
	public static void generateFiles() {
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
