package uk.ac.ed.inf.powergrab;

public class Station {
	public String id;
	private double power;
	private double coins;
	public String markerSymbol;
	public String markerColor;	
	public Position coordinates;
	
	public Station(String id, Position coordinates, double coins, double power, String markerSymbol, String markerColor) {
		this.id = id;
		this.coordinates = coordinates;
		this.power = power;
		this.coins = coins;
		this.markerColor = markerColor;
		this.markerSymbol = markerSymbol;
	}
	
	/**
	 * method to check if the type of station hasn't changed
	 */
	public void updateSymbol() {
		if (power < 0)
			markerSymbol = "danger";
		else
			markerSymbol = "lighthouse";
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public double getCoins() {
		return coins;
	}

	public void setCoins(double coins) {
		this.coins = coins;
	}
	
	public boolean isPositive() {
		return markerSymbol.equals("lighthouse");
	}
}
