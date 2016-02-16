package me.laekh.soulplots.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import me.laekh.soulplots.SoulPlots;

public class QuitListener implements Listener {
	
	 @EventHandler
	 public void onQuit(PlayerQuitEvent e) {
		 SoulPlots.getPlots().forget(e.getPlayer().getUniqueId());
	 }

}
