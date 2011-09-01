package org.michalpol.HealthExtended;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Health Extended Plugin
 * @author michalpol
 * @version 0.0.2
 */

public class HealthExtendedPlayerListener extends PlayerListener{
	public static HealthExtended plugin; public HealthExtendedPlayerListener(HealthExtended instance) {
        plugin = instance;
}	
	
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
			HealthExtendedPlayerListener.plugin.handlePlayerEat(event.getPlayer(), event.getPlayer()
					.getItemInHand());
			break;
		case RIGHT_CLICK_BLOCK:
			try {
				if (event.hasItem()
						&& HealthExtendedPlayerListener.plugin.foods.containsKey(event
								.getPlayer().getItemInHand().getType().name())) {
					HealthExtendedPlayerListener.plugin.handlePlayerEat(event.getPlayer(), event
							.getPlayer().getItemInHand());
					return;
				}
			} catch (NullPointerException e) {
				return;
			}
			Block block = event.getClickedBlock();
			HealthExtendedPlayerListener.plugin.handlePlayerEat(event.getPlayer(), block);
			break;
		}
	}

	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player=event.getPlayer();
		plugin.healplayer(player, 200);
	}
	
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		plugin.loaddata(event.getPlayer());
	}
	
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.savedata(event.getPlayer());
	}
}
