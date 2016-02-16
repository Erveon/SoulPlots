package me.laekh.soulplots.listener;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Directional;

import me.laekh.soulplots.SoulPlots;
import me.laekh.soulplots.plots.PlotChunk;

public class SignPlacer implements Listener {
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if(!e.getPlayer().isOp())
			return;
		//Setting up a sign location
		if(e.getLine(0).equalsIgnoreCase("Plot")) {
			//Check if it's valid
			BlockFace facing = getFacing(e.getBlock()).getOppositeFace();
			Location loc = e.getBlock().getLocation().add(facing.getModX(), facing.getModY(), facing.getModZ());
			Chunk chunk = loc.getChunk();
			if(!(e.getBlock().getState() instanceof Sign)) 
				return;
			if(SoulPlots.getChunks().contains(chunk)) {
				e.getPlayer().sendMessage("§aYou have placed a plot sign!");
				PlotChunk pChunk = SoulPlots.getChunks().get(chunk);
				pChunk.addSign(e.getBlock().getLocation());
				e.setLine(0, "§7- §9Plot §7-");
				e.setLine(1, "Click to claim");
				e.setLine(2, "this plot!");
			} else {
				e.getPlayer().sendMessage("§4Please place plot signs in front plot chunks facing outward.");
			}
		}
	}

	@SuppressWarnings("deprecation")
	public BlockFace getFacing(Block b) {
        return ((Directional) b.getType().getNewData(b.getData())).getFacing();
    }

}
