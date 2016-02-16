package me.laekh.soulplots.plots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;

import me.laekh.soulplots.SoulPlots;
import me.laekh.soulplots.plots.data.BlockObject;
import me.laekh.soulplots.plots.data.block.BlockInventoryHolder;
import me.laekh.soulplots.plots.data.block.BlockMobSpawner;
import me.laekh.soulplots.plots.data.block.BlockSign;

@SuppressWarnings("deprecation")
public class PlotManager {
	
	Map<UUID, Integer> cooldown;
	Map<UUID, PlotChunk> placedPlots;
	Map<UUID, Map<Integer, Plot>> plots; 
	
	public PlotManager() { 
		plots = new HashMap<>();
		placedPlots = new HashMap<>();
		cooldown = new HashMap<>();
		handlecooldown();
	}
	
	public void handlecooldown() {
		new BukkitRunnable() {

			@Override
			public void run() {
				Map<UUID, Integer> newCooldowns = new HashMap<>();
				Map<UUID, Integer> cooldowns = new HashMap<>();
				cooldowns.putAll(cooldown);
				for(Entry<UUID, Integer> cd : cooldowns.entrySet()) {
					if(cd.getValue() > 0)
						newCooldowns.put(cd.getKey(), cd.getValue() - 1);
				}
				cooldown = newCooldowns;
			}
			
		}.runTaskTimerAsynchronously(SoulPlots.getInstance(), 20, 20);
	}
	
	public boolean hasCooldown(UUID uuid) {
		return cooldown.containsKey(uuid);
	}
	
	public int getCooldown(UUID uuid) {
		if(hasCooldown(uuid))
			return cooldown.get(uuid);
		return 0;
	}
	
	public void addCooldown(UUID uuid, int seconds) {
		cooldown.put(uuid, seconds);
	}
	
	//COUNT STARTS FROM 0!!
	public Plot getPlot(UUID uuid, int count) {
		if(plots.containsKey(uuid)) {
			Map<Integer, Plot> map = plots.get(uuid);
			if(map.containsKey(count))
				return map.get(count);
		}
		Plot loaded = load(uuid, count);
		Map<Integer, Plot> newMap = new HashMap<>();
		if(plots.containsKey(uuid))
			newMap.putAll(plots.get(uuid));
		newMap.put(count, loaded);
		plots.put(uuid, newMap);
		return loaded;
	}
	
	public void forget(UUID uuid) {
		if(plots.containsKey(uuid))
			plots.remove(uuid);
	}
	
	private void savePlot(PlotChunk pChunk, Plot plot) {
		Chunk chunk = pChunk.getChunk();
		if(chunk == null)
			return;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 100; y++) {
					//Current block to save
					Block block = chunk.getBlock(x, pChunk.getFloor() - 50 + y, z);
					plot.setBlock(block, x, y, z);
				}
			}
		}
	}
	
	public void deconstructPlot(PlotChunk chunk) {
		//Also saves it
		if(chunk.hasPlot()) {
			savePlot(chunk, chunk.getPlot());
			UUID owner = chunk.getPlot().getOwner();
			if(placedPlots.containsKey(owner))
				placedPlots.remove(owner);
		}
		chunk.setPlot(null);
		for(Location signLoc : chunk.getSigns()) {
			Block sBlock = signLoc.getBlock();
			if(sBlock.getType().equals(Material.SIGN_POST)) {
				Sign s = (Sign) sBlock.getState();
				s.setLine(1, "Click to claim");
				s.setLine(2, "this plot!");
				s.update();
			}
		}
		SoulPlots.getChunks().generateEmptyPlot(chunk.getChunk(), chunk.getFloor());
	}
	
	public void buildPlot(final Plot plot, Chunk chunk) {
		PlotChunk pChunk = SoulPlots.getChunks().get(chunk);
		Player p = Bukkit.getPlayer(plot.getOwner());
		if(p == null)
			return;
		if(pChunk == null) {
			p.sendMessage("§4Your plot cannot be built here :(");
			return;
		}
		
		if(hasCooldown(plot.getOwner())) {
			p.sendMessage("§9You have to wait another §e" + getCooldown(plot.getOwner()) +" §9seconds before claiming a plot again.");
			return;
		}
		
		p.sendMessage("§aYou have successfully claimed the plot!");
		p.sendMessage("§aClick the sign again to unclaim it, so others can use it too.");
		addCooldown(plot.getOwner(), 30);

		for(Location signLoc : pChunk.getSigns()) {
			Block sBlock = signLoc.getBlock();
			if(sBlock.getType().equals(Material.SIGN_POST)) {
				Sign s = (Sign) sBlock.getState();
				s.setLine(1, "Occupied by");
				s.setLine(2, p.getName());
				s.update();
			}
		}
		
		if(placedPlots.containsKey(plot.getOwner())) {
			PlotChunk previousChunk = placedPlots.get(plot.getOwner());
			deconstructPlot(previousChunk);
		}
		placedPlots.put(plot.getOwner(), pChunk);
		
		pChunk.setPlot(plot);
		initStarter(pChunk);
		
		Map<Location, BlockObject> later = new HashMap<>();
		
		plot.setBuilt(false);
		Object[][][] blocks = plot.getBlockData();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 100; y++) {
			
					Object state = blocks[x][y][z];
					
					if(state == null)
						continue;
					
					// The block we will be updating
					Block block = chunk.getBlock(x, pChunk.getFloor() - 50 + y, z);
					
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
						
						boolean shouldFill = true;
						
						if(block.getState() instanceof Chest) {
							Object nextState = null;
							if(x != 16)
								nextState = blocks[x + 1][y][z];
							if(nextState != null) {
								Block nextBlock = chunk.getBlock(x + 1, pChunk.getFloor() - 50 + y, z);
								if(nextState instanceof BlockInventoryHolder) {
									BlockInventoryHolder nextContainer = (BlockInventoryHolder) state;
									nextBlock.setTypeIdAndData(nextContainer.getTypeId(), nextContainer.getData(), false);
									if(nextBlock.getState() instanceof Chest) 
										shouldFill = false;
								}
							}
							if(z != 16)
								nextState = blocks[x][y][z + 1];
							if(nextState != null) {
								Block nextBlock = chunk.getBlock(x, pChunk.getFloor() - 50 + y, z + 1);
								if(nextState instanceof BlockInventoryHolder) {
									BlockInventoryHolder nextContainer = (BlockInventoryHolder) state;
									nextBlock.setTypeIdAndData(nextContainer.getTypeId(), nextContainer.getData(), false);
									if(nextBlock.getState() instanceof Chest)
										shouldFill = false;
								}
							}
						}
						
						if(shouldFill) {
							// Container to receive the inventory
							InventoryHolder container = (InventoryHolder) block.getState();
							
							// Contents we are respawning.						
							container.getInventory().setContents(containerData.getInventory().getContents());
						}
					} else {
						BlockObject blockData = (BlockObject) state;
						if(blockData.getTypeId() == 50 || blockData.getTypeId() == 75 || blockData.getTypeId() == 76)
							later.put(block.getLocation(), blockData);
						else
							block.setTypeIdAndData(blockData.getTypeId(), blockData.getData(), false);
					}
				}
			}
		}
		for(Entry<Location, BlockObject> b : later.entrySet()) {
			b.getKey().getBlock().setTypeIdAndData(b.getValue().getTypeId(), b.getValue().getData(), false);
		}
		new BukkitRunnable() {

			@Override
			public void run() {
				plot.setBuilt(true);
			}
			
		}.runTaskLater(SoulPlots.getInstance(), 20 * 3);
	}
	
	public void save(PlotChunk chunk) {
		Plot plot = chunk.getPlot();
		if(plot == null)
			return;
		savePlot(chunk, plot);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(SoulPlots.getSettings().getPlotPath() + "/"+ plot.getOwner() + plot.getCount() + ".dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(plot);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Plot load(UUID owner, int count) {
		FileInputStream fis;
		try {
			File file = new File(SoulPlots.getSettings().getPlotPath()  + "/"+ owner + count + ".dat");
			if(!file.exists())
				return new Plot(owner, count);
			fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj_in = ois.readObject();
			if(obj_in instanceof Plot) {
				Plot loadedPlot = (Plot) obj_in;
				ois.close();
				return loadedPlot;
			}
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public void initStarter(PlotChunk chunk) {
		if(chunk.hasPlot()) {
			Plot plot = chunk.getPlot();
			if(plot.isNew()) {
				Location pasteLoc = chunk.getChunk().getBlock(0, chunk.getFloor(), 0).getLocation().add(6, 0, 15);
				 try {
			            File schem = SoulPlots.getSettings().getStarterHouse();
			            EditSession editSession = new EditSession(new BukkitWorld(pasteLoc.getWorld()), 999999999);
			            editSession.enableQueue();
			            SchematicFormat schematic = SchematicFormat.getFormat(schem);
			            CuboidClipboard clipboard = schematic.load(schem);
			            clipboard.paste(editSession, BukkitUtil.toVector(pasteLoc), true);
			            editSession.flushQueue();
						plot.setNew(false);
			        } catch (DataException | IOException ex) {
			            ex.printStackTrace();
			        } catch (MaxChangedBlocksException ex) {
			            ex.printStackTrace();
			        }
			}
		}
	}

}
