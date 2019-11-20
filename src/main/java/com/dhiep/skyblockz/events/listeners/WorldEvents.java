package com.dhiep.skyblockz.events.listeners;

import com.dhiep.skyblockz.slimeworld.SlimeWorldManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldEvents implements Listener {
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();

        SlimeWorldManager.worldLoadProcess(world);
    }
}
