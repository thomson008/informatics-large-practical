package uk.ac.ed.inf.powergrab;

public class Station {
	public String id;
	public double power;
	public double coins;
	public String markerSymbol;
	public String markerColor;	
	public Position coordinates;
	
	public Station(Position coordinates, double coins, double power, String markerSymbol, String markerColor) {
		this.coordinates = coordinates;
		this.power = power;
		this.coins = coins;
		this.markerColor = markerColor;
		this.markerSymbol = markerSymbol;
	}

}
