/**
 * 
 */
package me.laekh.soulplots.plots.data.block;

import org.bukkit.inventory.Inventory;
import me.laekh.soulplots.plots.data.BlockObject;
import me.laekh.soulplots.plots.data.InventoryObject;

public class BlockInventoryHolder extends BlockObject {

	private static final long serialVersionUID = -2217901027406106557L;
	
	InventoryObject inventory;

	/**
	 * Constructor for all Container objects
	 * 
	 * @param typeId
	 * @param items
	 */
	public BlockInventoryHolder(int typeId, Inventory inv) {
		super(typeId);
		setInventory(inv);
	}

	/**
	 * Constructor for all Container objects
	 * 
	 * @param typeId
	 * @param data
	 * @param items
	 */
	public BlockInventoryHolder(int typeId, byte data, Inventory inv) {
		super(typeId, data);
		setInventory(inv);
	}

	public Inventory getInventory() {
		return inventory.get();
	}

	public void setInventory(Inventory inv) {
		inventory = new InventoryObject(inv);
	}
}
