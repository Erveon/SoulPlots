package me.laekh.soulplots.listener;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.laekh.soulplots.SoulPlots;
import me.laekh.soulplots.plots.Plot;
import me.laekh.soulplots.plots.PlotChunk;

public class BlockListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onBuild(BlockPlaceEvent e) {
		Chunk c = e.getBlock().getLocation().getChunk();
		if(SoulPlots.getChunks().contains(c)) {
			PlotChunk pChunk = SoulPlots.getChunks().get(c);
			if(pChunk.hasPlot()) {
				Plot p = pChunk.getPlot();
				if(p.getOwner().equals(e.getPlayer().getUniqueId())) {
					e.setCancelled(false);
					if(e.getBlock().getLocation().getBlockY() < pChunk.getFloor() - 50) {
						e.setCancelled(true);
						e.getPlayer().sendMessage("§6You can only build 50 blocks below the floor.");
					} else if(e.getBlock().getLocation().getBlockY() > pChunk.getFloor() + 50) {
						e.setCancelled(true);
						e.getPlayer().sendMessage("§6You can only build 50 blocks above the floor.");
					}
				} else {
					e.setCancelled(true);
					e.getPlayer().sendMessage("§6This is not your area! You can claim an area elsewhere for your plot.");
				}
			} else {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§6Claim the area before building here. (Look for a sign)");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onBreak(BlockBreakEvent e) {
		Chunk c = e.getBlock().getLocation().getChunk();
		if(SoulPlots.getChunks().contains(c)) {
			PlotChunk pChunk = SoulPlots.getChunks().get(c);
			if(pChunk.hasPlot()) {
				Plot p = pChunk.getPlot();
				if(p.getOwner().equals(e.getPlayer().getUniqueId())) {
					e.setCancelled(false);
					if(e.getBlock().getLocation().getBlockY() < pChunk.getFloor() - 50) {
						e.setCancelled(true);
						e.getPlayer().sendMessage("§6You can only break 50 blocks below the floor.");
					} else if(e.getBlock().getLocation().getBlockY() > pChunk.getFloor() + 50) {
						e.setCancelled(true);
						e.getPlayer().sendMessage("§6You can only break 50 blocks above the floor.");
					}
				} else {
					e.setCancelled(true);
					e.getPlayer().sendMessage("§6This is not your area! You can claim an area elsewhere for your plot.");
				}
			} else {
				e.setCancelled(true);
				e.getPlayer().sendMessage("§6Claim the area before breaking here. (Look for a sign)");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBroken(BlockPhysicsEvent e) {
		Chunk c = e.getBlock().getLocation().getChunk();
		if(SoulPlots.getChunks().contains(c)) {
			PlotChunk pChunk = SoulPlots.getChunks().get(c);
			if(!pChunk.hasPlot()) {
				e.setCancelled(true);
			} else {
				if(!pChunk.getPlot().isBuilt())
					e.setCancelled(true);
			}
		}
	}

}
