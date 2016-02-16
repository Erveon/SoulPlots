package me.laekh.soulplots.commands;

import org.bukkit.command.CommandSender;

public interface SubCommand
{
	
    boolean onCommand(CommandSender sender, String[] args);
    
}
