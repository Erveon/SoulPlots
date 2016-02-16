package me.laekh.soulplots;

import java.util.Map.Entry;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import me.laekh.soulplots.commands.CommandHandler;
import me.laekh.soulplots.listener.BlockListener;
import me.laekh.soulplots.listener.ChunkListener;
import me.laekh.soulplots.listener.QuitListener;
import me.laekh.soulplots.listener.SignClickListener;
import me.laekh.soulplots.listener.SignPlacer;
import me.laekh.soulplots.plots.ChunkManager;
import me.laekh.soulplots.plots.PlotChunk;
import me.laekh.soulplots.plots.PlotManager;

public class SoulPlots extends JavaPlugin {

	private static SoulPlots instance;
	private static PlotManager plotManager;
	private static ChunkManager chunkManager;
	private static Settings settings;
	
	private static WorldEditPlugin we;
	
	@Override
	public void onEnable() {
		instance = this;
		
		settings = new Settings();
		chunkManager = new ChunkManager();
		plotManager = new PlotManager();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new QuitListener(), this);
		pm.registerEvents(new SignPlacer(), this);
		pm.registerEvents(new SignClickListener(), this);
		pm.registerEvents(new ChunkListener(), this);
		pm.registerEvents(new BlockListener(), this);
		
		setupCommands();
		
		for(Chunk c : getChunks().getChunks().keySet()) {
			if(!c.isLoaded())
				c.load();
		}
		
		setupWorldEdit();
	}
	
	@Override
	public void onDisable() {
		for(Entry<Chunk, PlotChunk> chunk : getChunks().getChunks().entrySet()) {
			getPlots().save(chunk.getValue());
			getChunks().generateEmptyPlot(chunk.getKey(), chunk.getValue().getFloor());
			for(Location l : chunk.getValue().getSigns()) {
				if(l.getBlock().getType().equals(Material.SIGN_POST)) {
					Sign s = (Sign) l.getBlock().getState();
					s.setLine(0, "§7- §9Plot §7-");
					s.setLine(1, "Click to claim");
					s.setLine(2, "this plot!");
					s.update();
				}
			}
		}
	}
	
	public static WorldEditPlugin getWorldEdit() {
		return we;
	}
	
	public void setupCommands() {
		getCommand("soulplots").setExecutor(new CommandHandler());
	}
	
	public static SoulPlots getInstance() {
		return instance;
	}
	
	public static PlotManager getPlots() {
		return plotManager;
	}
	
	public static ChunkManager getChunks() {
		return chunkManager;
	}
	
	public static Settings getSettings() {
		return settings;
	}
	
	private void setupWorldEdit() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin == null || !(plugin instanceof WorldEditPlugin))
            return;
 
        we = (WorldEditPlugin) plugin;
    }
	
}