package com.dhiep.skyblockz;

import com.dhiep.skyblockz.database.PlayerData;
import org.bukkit.Bukkit;

public class TaskManager {
    public static void init(SkyblockZ instance) {
        Bukkit.getScheduler().runTaskTimer(instance, PlayerData::saveOnlinePlayers,20*60, 20*60);
    }
}
