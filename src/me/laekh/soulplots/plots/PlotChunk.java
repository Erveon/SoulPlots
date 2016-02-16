package me.laekh.soulplots.plots;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import me.laekh.soulplots.SoulPlots;
import me.laekh.soulplots.plots.data.BlockObject;
import me.laekh.soulplots.plots.data.block.BlockInventoryHolder;
import me.laekh.soulplots.plots.data.block.BlockMobSpawner;
import me.laekh.soulplots.plots.data.block.BlockSign;

//Used to save the chunk that was present before turning into a plotchunk
public class PlotChunk implements Serializable {

	private static final long serialVersionUID = -3672933845622126082L;
	
	private int X_SIZE = 16;
	private int Y_SIZE = 256;
	private int Z_SIZE = 16;
	
	BlockObject[][][] blockData = new BlockObject[X_SIZE][Y_SIZE][Z_SIZE];
	
	private String world;
	private int x;
	private int z;
	private int floor;
	private Plot plot;
	private List<Location> signs;

	public PlotChunk(Chunk chunk, int floor) {
		this.world = chunk.getWorld().getName();
		this.x = chunk.getX();
		this.z = chunk.getZ();
		this.floor = floor; 
	}
	
	public String getWorld() {
		return world;
	}
	
	public int getX() {
		return x;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public void removeSign(Location loc) {
		if(signs == null)
			return;
		if(signs.contains(loc))
			signs.remove(loc);
	}
	
	public List<Location> getSigns() {
		if(signs == null)
			signs = new ArrayList<>();
		return signs;
	}
	
	public void addSign(Location loc) {
		if(signs == null)
			this.signs = new ArrayList<>();
		if(loc == null)
			return;
		signs.add(loc);
	}
	
	public void setPlot(Plot plot) {
		this.plot = plot;
	}
	
	public Plot getPlot() {
		return plot;
	}
	
	public boolean hasPlot() {
		return plot != null;
	}
	
	public void save() {
		Chunk chunk = getChunk();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					setBlock(chunk.getBlock(x, y, z), x, y, z);
				}
			}
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(SoulPlots.getSettings().getChunkPath() + world + ":" + x + ":" + z + ".dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Chunk getChunk() {
		World world = Bukkit.getWorld(getWorld());
		if(world == null)
			return null;
		return world.getChunkAt(getX(), getZ());
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
	
	@SuppressWarnings("deprecation")
	public void revert() {
		Chunk chunk = getChunk();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					Object state = blockData[x][y][z];
					
					// The block we will be updating
					Block block = chunk.getBlock(x, y, z);
					
					if (state instanceof BlockSign) {
						BlockSign signData = (BlockSign)state;
						block.setTypeIdAndData(signData.getTypeId(), signData.getData(), false);
						
						Sign sign = (Sign) block.getState();
						int i = 0;
						for (String line : signData.getLines())
							sign.setLine(i++, line);
						
						sign.update(true);
					} else if (state instanceof BlockMobSpawner) {
						
						BlockMobSpawner spawnerData = (BlockMobSpawner) state;
	
						block.setTypeIdAndData(spawnerData.getTypeId(), spawnerData.getData(), false);
						((CreatureSpawner) block.getState()).setSpawnedType(spawnerData.getSpawnedType());
						((CreatureSpawner) block.getState()).setDelay(spawnerData.getDelay());
					} else if ((state instanceof BlockInventoryHolder) && !(state instanceof Player)) {
						
						BlockInventoryHolder containerData = (BlockInventoryHolder) state;
						block.setTypeIdAndData(containerData.getTypeId(), containerData.getData(), false);
						
						// Container to receive the inventory
						InventoryHolder container = (InventoryHolder) block.getState();
						
						// Contents we are respawning.						
						container.getInventory().setContents(containerData.getInventory().getContents());
					} else {
						BlockObject blockData = (BlockObject) state;
						block.setTypeIdAndData(blockData.getTypeId(), blockData.getData(), false);
					}
				}
			}
		}
	}
	
}
