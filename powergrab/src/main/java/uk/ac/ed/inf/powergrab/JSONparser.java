package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
/**
 * 
 * @author Tomek
 *
 */
public class JSONparser {
	public static FeatureCollection features;
	public static List<Feature> featureList;
	public static List<Point> points = new ArrayList<>();
	public static LineString ls;
	public static Feature route;
	/**
	 * 
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private static String readJsonFromUrl(String url) throws IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			return jsonText;
		} finally {
			is.close();
		}
	}
	
	/**
	 * 
	 * @param url String representation of the URL where the JSON file resides
	 * @return List of Station objects parsed from the JSON
	 * @throws IOException
	 */
	public static List<Station> parseJson(String url) throws IOException {
		String json = readJsonFromUrl(url);
		features = FeatureCollection.fromJson(json);
		featureList = features.features();
		ArrayList<Station> stationList = new ArrayList<>();
		
		for (Feature feature : featureList) {
			Point coordinates = (Point) feature.geometry();
			
			String id = feature.getStringProperty("id");
			String symbol = feature.getStringProperty("marker-symbol");
			String color = feature.getStringProperty("marker-color");
			
			double coins = feature.getNumberProperty("coins").doubleValue();
			double power = feature.getNumberProperty("power").doubleValue();
			double longitude = coordinates.longitude();
			double latitude = coordinates.latitude();
			
			Position stationPosition = new Position(latitude, longitude);
			Station newStation = new Station(id, stationPosition, coins, power, symbol, color);
			stationList.add(newStation);
		}
		
		return stationList;
	}
	
	public static FeatureCollection addRouteToFeatures() {
		ls = LineString.fromLngLats(points);
		route = Feature.fromGeometry(ls);
		route.addProperty("", null);
		featureList.add(route);
		return FeatureCollection.fromFeatures(featureList);
	}
}