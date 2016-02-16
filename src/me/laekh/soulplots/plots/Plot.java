package me.laekh.soulplots.plots;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import me.laekh.soulplots.plots.data.BlockObject;
import me.laekh.soulplots.plots.data.EntityObject;
import me.laekh.soulplots.plots.data.block.BlockInventoryHolder;
import me.laekh.soulplots.plots.data.block.BlockMobSpawner;
import me.laekh.soulplots.plots.data.block.BlockSign;
import me.laekh.soulplots.utils.Vector;

public class Plot implements Serializable {

	private static final long serialVersionUID = 8125483103966748030L;
	
	private int X_SIZE = 16;
	private int Y_SIZE = 100;
	private int Z_SIZE = 16;
	
	private BlockObject[][][] blockData = new BlockObject[X_SIZE][Y_SIZE][Z_SIZE];
	private List<EntityObject> entityData;
	
	private UUID owner;
	private int count;
	
	private boolean isnew;
	private boolean isbuilt = false;
	
	public Plot(UUID owner, int count) {
		this.owner = owner;
		this.count = count;
		this.entityData = new ArrayList<>();
		this.isnew = true;
		this.isbuilt = false;
	}
	
	public boolean isBuilt() {
		return isbuilt;
	}
	
	public boolean isNew() {
		return isnew;
	}
	
	public void setBuilt(boolean isbuilt) {
		this.isbuilt = isbuilt;
	}
	
	public void setNew(boolean isnew) {
		this.isnew = isnew;
	}
	
	public UUID getOwner() {
		return owner;
	}
	
	public int getCount() {
		return count;
	}
	
	public BlockObject[][][] getBlockData() {
		return blockData;
	}
	
	public List<EntityObject> getEnties() {
		return entityData;
	}
	
	@SuppressWarnings("deprecation")
	public void setBlock(Block block, int x, int y, int z) {
		BlockState state = block.getState();
		if (state instanceof org.bukkit.block.Sign) {
			BlockSign sign = new BlockSign(state.getTypeId(), state.getRawData(), ((org.bukkit.block.Sign) state).getLines());
			sign.setVector(state.getLocation());
			blockData[x][y][z] = sign;
		} else if (state instanceof CreatureSpawner) {
			BlockMobSpawner spawner = new BlockMobSpawner(((CreatureSpawner) state).getSpawnedType());
			spawner.setVector(state.getLocation());
			spawner.setDelay(((CreatureSpawner) state).getDelay());
			blockData[x][y][z] = spawner;
		} else if ((state instanceof InventoryHolder) && !(state instanceof Player)) {
			BlockInventoryHolder holder = new BlockInventoryHolder(state.getTypeId(), state.getRawData(), ((InventoryHolder) state).getInventory());
			holder.setVector(state.getLocation());
			blockData[x][y][z] = holder;
		} else {
			blockData[x][y][z] = new BlockObject(state.getTypeId(), state.getRawData(), state.getLocation());
		}
	}
	
	public BlockObject getBlock(int x, int y, int z) {
		return blockData[x][y][z];
	}
	
	public void addEntity(Entity entity) {
		entityData.add(new EntityObject(entity.getType(), new Vector(entity.getLocation())));
	}
	
	public void removeEntity(Entity entity) {
		if(entityData.contains(entity))
			entityData.remove(entity);
	}
	
}
