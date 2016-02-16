package me.laekh.soulplots.plots;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.laekh.soulplots.SoulPlots;

public class ChunkManager {
	
	Map<Chunk, PlotChunk> chunks;
	
	public ChunkManager() {
		chunks = new HashMap<>();
		FileInputStream fis;
		for(File f : new File(SoulPlots.getSettings().getChunkPath()).listFiles()) {
			try {
				fis = new FileInputStream(f);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Object obj_in = ois.readObject();
				if(obj_in instanceof PlotChunk) {
					PlotChunk pChunk = (PlotChunk) obj_in;
					chunks.put(pChunk.getChunk(), pChunk);
				}
				ois.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
	}
	
	public Map<Chunk, PlotChunk> getChunks() {
		return chunks;
	}
	
	public PlotChunk get(Chunk chunk) {
		if(contains(chunk))
			return chunks.get(chunk);
		return null;
	}
	
	public boolean contains(Chunk chunk) {
		return chunks.containsKey(chunk);
	}

	public void add(Player p, Chunk chunk) {
		if(contains(chunk))
			return;
		PlotChunk plotChunk = new PlotChunk(chunk, p.getLocation().getBlockY());
		chunks.put(chunk, plotChunk);
		plotChunk.save();
		generateEmptyPlot(chunk, p.getLocation().getBlockY());
	}
	
	@SuppressWarnings("deprecation")
	public void generateEmptyPlot(Chunk chunk, int maxY) {
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					BlockState state = chunk.getBlock(x, y, z).getState();
					if(state instanceof Chest) {
						Chest chest = (Chest) state;
						chest.getBlockInventory().clear();
					}
					if(y < maxY) {
						chunk.getBlock(x, y, z).setTypeIdAndData(35, (byte) 7, false);
					} else if(y == 0) {
						chunk.getBlock(x, y, z).setType(Material.BEDROCK);
					} else {
						chunk.getBlock(x, y, z).setType(Material.AIR);
					}
				}
			}
		}
		for(Entity e : chunk.getEntities()) {
			if(!(e instanceof Player))
				e.remove();
		}
	}
	
	public void remove(Chunk chunk) {
		if(!contains(chunk))
			return;
		PlotChunk loadedChunk = null;
		FileInputStream fis;
		try {
			fis = new FileInputStream(SoulPlots.getSettings().getChunkPath() + chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ() + ".dat");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj_in = ois.readObject();
			if(obj_in instanceof PlotChunk)
				loadedChunk = (PlotChunk) obj_in;
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return;
		} 
		if(loadedChunk != null) {
			loadedChunk.revert();
			File file = new File(SoulPlots.getSettings().getChunkPath() + chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ() + ".dat");
			if(file.delete())
				chunks.remove(chunk);
		} else {
			System.out.println("Could not find the chunk file..");
		}
	}

}
