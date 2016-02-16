/**
 * 
 */
package me.laekh.soulplots.plots.data.block;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.laekh.soulplots.plots.data.BlockObject;

/**
 * @author ElgarL
 * 
 */
public class BlockMobSpawner extends BlockObject {

	private static final long serialVersionUID = 1049441962244117724L;
	
	private EntityType mobType;
	private int delay;

	/**
	 * @param type
	 */
	@SuppressWarnings("deprecation")
	public BlockMobSpawner(EntityType type) {
		super(Material.MOB_SPAWNER.getId());
		this.mobType = type;
	}

	/**
	 * Get the mob type.
	 * 
	 * @return the EntityType this spawner is set for.
	 */
	public EntityType getSpawnedType() {

		return mobType;
	}

	/**
	 * Set the mob type.
	 * 
	 * @param mobType
	 */
	public void setSpawnedType(EntityType mobType) {

		this.mobType = mobType;
	}

	/**
	 * @return the delay
	 */
	public int getDelay() {

		return delay;
	}

	/**
	 * @param i the delay to set
	 */
	public void setDelay(int i) {

		this.delay = i;
	}

}
