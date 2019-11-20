package com.dhiep.skyblockz.events.listeners;

import com.dhiep.skyblockz.database.PlayerData;
import com.dhiep.skyblockz.islands.IslandManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        PlayerData.loadPlayer(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        IslandManager.unloadIsland(player);
        PlayerData.savePlayerQuit(player);
    }
}
