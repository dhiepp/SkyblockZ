package com.dhiep.skyblockz;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import com.dhiep.skyblockz.commands.CommandManager;
import com.dhiep.skyblockz.configs.ConfigManager;
import com.dhiep.skyblockz.database.DataConnection;
import com.dhiep.skyblockz.events.EventManager;
import com.dhiep.skyblockz.slimeworld.SlimeWorldManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockZ extends JavaPlugin {
    private static SkyblockZ instance;
    private static TaskChainFactory taskChainFactory;

    @Override
    public void onEnable() {
        instance = this;
        taskChainFactory = BukkitTaskChainFactory.create(this);

        ConfigManager.init(instance);
        DataConnection.getConnection();
        CommandManager.init(instance);
        EventManager.init(instance);
        TaskManager.init(instance);

        SlimeWorldManager.init();
    }

    @Override
    public void onDisable() {
        ConfigManager.saveAllConfigs();
        DataConnection.close();
    }

    public static SkyblockZ getInstance() {
        return instance;
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }
    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }
}
