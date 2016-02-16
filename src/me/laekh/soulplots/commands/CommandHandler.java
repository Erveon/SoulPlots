package me.laekh.soulplots.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor
{
    private HashMap<String, SubCommand> commands;
    
    public CommandHandler() {
        commands = new HashMap<String, SubCommand>();
        loadCommands();
    }
    
    private void loadCommands() {
        this.commands.put("chunk", new ChunkCommand());
        this.commands.put("build", new BuildCommand());
        this.commands.put("deconstruct", new DeconstructCommand());
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, String[] args) {
        if (args == null || args.length < 1) {
        	if(sender instanceof Player)
        		sender.sendMessage("§6SoulPlots - Developed by Laekh | laekh@hotmail.com");
            return true;
        }
        
        final String sub = args[0];
        final Vector<String> arguments = new Vector<String>();
        arguments.addAll(Arrays.asList(args));
        arguments.remove(0);
        args = arguments.toArray(new String[0]);
        if (!this.commands.containsKey(sub)) {
        	sender.sendMessage("§4That command does not exist.");
            return true;
        }
        try {
            this.commands.get(sub).onCommand(sender, args);
        }
        catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage("§4An error has occured.");
        }
        return true;
    }
}
