package com.dhiep.skyblockz.events;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.events.listeners.PlayerEvents;
import com.dhiep.skyblockz.events.listeners.WorldEvents;
import org.bukkit.plugin.PluginManager;

public class EventManager {
    public static void init(SkyblockZ instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        pm.registerEvents(new PlayerEvents(), instance);
        pm.registerEvents(new WorldEvents(), instance);
    }
}
