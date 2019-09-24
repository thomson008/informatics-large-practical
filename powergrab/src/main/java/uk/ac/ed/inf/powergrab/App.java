package uk.ac.ed.inf.powergrab;

import java.io.IOException;
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
    	
    	for (int i = 0; i < 3; i++) {
    		JsonURL += args[i] + "/";
    	}
    	
    	JsonURL += "powergrabmap.geojson";
        JSONparser.parseJSON(JsonURL);
    }
}
