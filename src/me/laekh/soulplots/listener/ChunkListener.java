package me.laekh.soulplots.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.laekh.soulplots.SoulPlots;

public class ChunkListener implements Listener {

	@EventHandler
	public void onUnload(ChunkUnloadEvent e) {
		if(SoulPlots.getChunks().contains(e.getChunk()))
			e.setCancelled(true);
	}
	
}
