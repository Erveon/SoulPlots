package me.laekh.soulplots.commands;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.laekh.soulplots.SoulPlots;
import me.laekh.soulplots.plots.Plot;
import me.laekh.soulplots.plots.PlotChunk;

public class BuildCommand implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player))
			return true;
		
		Player p = (Player) sender;
		
		if(!p.isOp())
			return true;
		
		Chunk chunk = p.getLocation().getChunk();
		
		PlotChunk pChunk = SoulPlots.getChunks().get(chunk);
		if(pChunk == null) {
			p.sendMessage("§4This is not a valid location.");
			return true;
		}
		
		Plot plot = SoulPlots.getPlots().getPlot(p.getUniqueId(), 0);
		SoulPlots.getPlots().buildPlot(plot, chunk);
		
		return true;
	}

}
