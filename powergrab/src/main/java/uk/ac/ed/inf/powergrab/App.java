package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 
 * @author th
 *
 */
public class App {
	public static List<Station> stations;
	
    public static void main(String[] args) throws IOException {
    	String jsonURL = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
    	Path path = Paths.get(args[2], args[1], args[0], "powergrabmap.geojson");
    	jsonURL += path.toString().replace("\\", "/");
        stations = JSONparser.parseJson(jsonURL);
        
        Drone drone;
        Position initialPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));
        
        if (args[6].equals("stateless"))
        	drone = new StatelessDrone(initialPosition);
        else if (args[6].equals("stateful"))
        	drone = new StatefulDrone(initialPosition);
        else
        	drone = new StatelessDrone(initialPosition);
    }
}
