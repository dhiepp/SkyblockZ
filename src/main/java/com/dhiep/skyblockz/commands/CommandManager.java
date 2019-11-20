package com.dhiep.skyblockz.commands;

import com.dhiep.skyblockz.SkyblockZ;

public class CommandManager {
    public static void init(SkyblockZ instance) {
        instance.getCommand("island").setExecutor(new IslandCommand());
        instance.getCommand("coop").setExecutor(new CoopCommand());
    }
}
