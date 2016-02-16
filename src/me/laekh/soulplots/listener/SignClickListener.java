package me.laekh.soulplots.listener;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Directional;

import me.laekh.soulplots.SoulPlots;
import me.laekh.soulplots.plots.Plot;
import me.laekh.soulplots.plots.PlotChunk;

public class SignClickListener implements Listener {
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Block b = e.getClickedBlock();
			if(!b.getType().equals(Material.SIGN_POST))
				return;
			BlockFace facing = getFacing(b).getOppositeFace();
			Location loc = b.getLocation().add(facing.getModX(), facing.getModY(), facing.getModZ());
			Chunk chunk = loc.getChunk();
			if(SoulPlots.getChunks().contains(chunk)) {
				PlotChunk pChunk = SoulPlots.getChunks().get(chunk);
				if(!pChunk.getSigns().contains(b.getLocation())) {
					Sign s = (Sign) e.getClickedBlock().getState();
					s.setLine(0, "§7- §9Plot §7-");
					s.setLine(1, "Click to claim");
					s.setLine(2, "this plot!");
					s.update();
					pChunk.addSign(b.getLocation());
				}
				if(pChunk.hasPlot()) {
					if(pChunk.getPlot().getOwner().equals(e.getPlayer().getUniqueId())) {
						SoulPlots.getPlots().deconstructPlot(pChunk);
					} else {
						e.getPlayer().sendMessage("§4That plot is already occupied!");
						return;
					}
				} else {
					Plot plot = SoulPlots.getPlots().getPlot(e.getPlayer().getUniqueId(), 0);
					SoulPlots.getPlots().buildPlot(plot, chunk);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public BlockFace getFacing(Block b) {
        return ((Directional) b.getType().getNewData(b.getData())).getFacing();
    }
	
}
