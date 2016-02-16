package me.laekh.soulplots.plots.data.item;

import java.io.Serializable;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemObject implements Serializable {

	private static final long serialVersionUID = 5387999625686409665L;
	
	private Material material;
	private short durability;
	private int amount;
	private String[] lore;
	private String name;
	
	private int location;
	
	public ItemObject(ItemStack is, int location) {
		this.material = is.getType();
		this.durability = is.getDurability();
		this.amount = is.getAmount();
		this.location = location;
		if(is.hasItemMeta()) {
			ItemMeta im = is.getItemMeta();
			if(im.hasDisplayName())
				name = im.getDisplayName();
			if(im.hasLore())
				lore = (String[]) im.getLore().toArray();
		}
	}
	
	public ItemStack get() {
		ItemStack is = new ItemStack(material, amount);
		is.setDurability(durability);
		ItemMeta im = is.getItemMeta();
		if(name != null)
			im.setDisplayName(name);
		if(lore != null)
			im.setLore(Arrays.asList(lore));
		return is;
	}
	
	public int getLocation() {
		return location;
	}
	
}
