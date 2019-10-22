package uk.ac.ed.inf.powergrab;

public class Station {
	public String id;
	private double power;
	private double coins;

	public Position coordinates;
	
	public Station(String id, Position coordinates, double coins, double power) {
		this.id = id;
		this.coordinates = coordinates;
		this.power = power;
		this.coins = coins;
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
		return coins >= 0;
	}
}
