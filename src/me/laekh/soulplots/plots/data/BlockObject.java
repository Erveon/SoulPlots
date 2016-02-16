package me.laekh.soulplots.plots.data;

import java.io.Serializable;

import org.bukkit.Location;

import me.laekh.soulplots.utils.Vector;

public class BlockObject implements Serializable {

	private static final long serialVersionUID = 5695270215784869706L;
	
	private int typeId;
	private byte data;
	private Vector vector;

	public BlockObject(int typeId) {
		this.typeId = typeId;
		this.data = 0;
	}
	
	public BlockObject(int typeId, Location loc) {
		this.typeId = typeId;
		this.data = 0;
		setVector(loc);
	}

	public BlockObject(int typeId, byte data) {
		this.typeId = typeId;
		this.data = data;
	}
	
	public BlockObject(int typeId, byte data, Location loc) {
		this.typeId = typeId;
		this.data = data;
		setVector(loc);
	}

	public int getTypeId() {
		return typeId;
	}

	/**
	 * @param typeId the id to set
	 */
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	/**
	 * @return the data
	 */
	public byte getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(byte data) {
		this.data = data;
	}
	
	/**
	 * @return the location
	 */
	public Vector getVector() {
		return vector;
	}

	/**
	 * @param loc the location to set
	 */
	public void setVector(Location loc) {
		this.vector = new Vector(loc);
	}

	/**
	 * @param typeId the typeId to set
	 */
	public void setTypeIdAndData(int typeId, byte data) {
		this.typeId = typeId;
		this.data = data;
	}

}