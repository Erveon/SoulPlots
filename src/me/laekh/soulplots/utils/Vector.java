package me.laekh.soulplots.utils;

import java.io.Serializable;

import org.bukkit.Location;

public class Vector implements Serializable {

	private static final long serialVersionUID = -6259346222918538654L;
	
	int x, y, z;
	
	public Vector(org.bukkit.util.Vector vector) {
		this.x = vector.getBlockX();
		this.y = vector.getBlockY();
		this.z = vector.getBlockZ();
	}
	
	public Vector(Location location) {
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}

}
