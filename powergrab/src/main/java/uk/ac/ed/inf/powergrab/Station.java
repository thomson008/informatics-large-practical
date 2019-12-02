package uk.ac.ed.inf.powergrab;

import java.util.Comparator;

public class Station {
	private double power;
	private double coins;
	public final Position coordinates;
	public static Comparator<Station> itemsCmp = new Comparator<Station>() {
		public int compare(Station s1, Station s2) {
			if (s1.getCoins() + s1.getPower() < s2.getCoins() + s2.getPower())
				return -1;
			else if (s1.getCoins() + s1.getPower() == s2.getCoins() + s2.getPower())
				return 0;
			else 
				return 1;
		}
	};
	
	public Station(Position coordinates, double coins, double power) {
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
		return coins >= 0 && power >= 0;
	}
}
