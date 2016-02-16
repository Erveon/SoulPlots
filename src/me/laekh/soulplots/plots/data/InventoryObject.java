package me.laekh.soulplots.plots.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.laekh.soulplots.plots.data.item.ItemObject;

public class InventoryObject implements Serializable {

	private static final long serialVersionUID = 2514944592413270820L;
	
	private InventoryType invType;
	private int size;
	private List<ItemObject> items;
	
	public InventoryObject(Inventory inv) {
		this.items = new ArrayList<>();
		this.size = inv.getSize();
		this.invType = inv.getType();
		for(int i = 0; i < size; i++) {
			ItemStack is = inv.getContents()[i];
			if(is != null)
				items.add(new ItemObject(is, i));
		}
	}
	
	public Inventory get() {
		Inventory inv;
		if(invType.equals(InventoryType.CHEST))
			inv = Bukkit.createInventory(null, size);
		else
			inv = Bukkit.createInventory(null, invType);
		for(ItemObject io : items)
			inv.setItem(io.getLocation(), io.get());
		return inv;
	}

}
