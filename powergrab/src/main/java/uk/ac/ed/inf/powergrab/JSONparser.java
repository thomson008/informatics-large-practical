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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author th
 *
 */
public class JSONparser {
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
	public static JSONObject readJsonFromUrl(String url) throws IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static List<Station> parseJSON(String url) throws IOException {
		JSONObject json = readJsonFromUrl(url);
		JSONArray features = json.getJSONArray("features");
		ArrayList<Station> stations = new ArrayList<>();

		for (int i = 0; i < features.length(); i++) {
			JSONObject feature = features.getJSONObject(i);
			Station newStation = createObject(feature);
			stations.add(newStation);
		}

		return stations;
	}
	/**
	 * 
	 * @param feature
	 * @return
	 */
	public static Station createObject(JSONObject feature) {
		JSONObject properties = (JSONObject) feature.get("properties");
		JSONObject geometry = (JSONObject) feature.get("geometry");
		JSONArray coordinates = geometry.getJSONArray("coordinates");

		String id = properties.getString("id");
		double coins = properties.getDouble("coins");
		double power = properties.getDouble("power");
		String symbol = properties.getString("marker-symbol");
		String color = properties.getString("marker-color");
		double longitude = coordinates.getDouble(0);
		double latitude = coordinates.getDouble(1);

		Position stationPosition = new Position(latitude, longitude);

		Station newStation = new Station(id, stationPosition, coins, power, symbol, color);
		return newStation;
	}
}