package me.laekh.soulplots.plots.data;

import java.io.Serializable;

import org.bukkit.entity.EntityType;

import me.laekh.soulplots.utils.Vector;

public class EntityObject implements Serializable {

	private static final long serialVersionUID = 8517419147866669408L;
	
	EntityType type;
	Vector vector;
	
	public EntityObject(EntityType type, Vector vector) {
		this.type = type;
		this.vector = vector;
	}
	
	public EntityType getType() {
		return type;
	}
	
	public Vector getVector() {
		return vector;
	}

}
