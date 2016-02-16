package me.laekh.soulplots.commands;

import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.laekh.soulplots.SoulPlots;

public class ChunkCommand implements SubCommand {

	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player))
			return true;
		
		Player p = (Player) sender;
		if(!p.isOp())
			return false;
		
		if(args.length != 1) {
			p.sendMessage("§4Usage: /plots chunk [Save/Revert]");
			return true;
		}
		
		if(args[0].equalsIgnoreCase("add")) {
			if(p.getLocation().getBlockY() < 51 || p.getLocation().getBlockY() > 205) {
				p.sendMessage("§4Plots cannot be made lower than Y51 or higher than Y205");
				return true;
			}
			Chunk chunk = p.getLocation().getChunk();
			if(SoulPlots.getChunks().contains(chunk)) {
				p.sendMessage("§4This chunk is already a plot chunk");
				return true;
			}
			SoulPlots.getChunks().add(p, chunk);
			p.sendMessage("§aChunk saved");
		} else if(args[0].equalsIgnoreCase("remove")) {
			Chunk chunk = p.getLocation().getChunk();
			if(!SoulPlots.getChunks().contains(chunk)) {
				p.sendMessage("§4This chunk is not a plot chunk");
				return true;
			}
			SoulPlots.getChunks().remove(chunk);
			p.sendMessage("§aChunk reverted");
		} else {
			p.sendMessage("§4Usage: /plots chunk [Save/Revert]");
			return true;
		}
		
		return true;
	}

}
