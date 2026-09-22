package net.tfminecraft.advancedresearch;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RPlayer {
	public Player p;
	public Integer mentalPoints;
	public Location currentStation;
	public Player getPlayer() {
		return p;
	}
	public void setPlayer(Player p) {
		this.p = p;
	}
	public Integer getMentalPoints() {
		return mentalPoints;
	}
	public void setMentalPoints(Integer mentalPoints) {
		this.mentalPoints = mentalPoints;
	}
	public Location getCurrentStation() {
		return currentStation;
	}
	public void setCurrentStation(Location currentStation) {
		this.currentStation = currentStation;
	}
}
