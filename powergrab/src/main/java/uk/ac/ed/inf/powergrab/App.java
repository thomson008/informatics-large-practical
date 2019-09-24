package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * 
 * @author th
 *
 */
public class App {
	public static ArrayList<Station> stations = new ArrayList<>();
	
    public static void main(String[] args) throws IOException {
    	String JsonURL = "http://homepages.inf.ed.ac.uk/stg/powergrab/";
    	Path path = Paths.get(args[2], args[1], args[0], "powergrabmap.geojson");
    	JsonURL += path.toString().replace("\\", "/");
        JSONparser.parseJSON(JsonURL);
    }
}
