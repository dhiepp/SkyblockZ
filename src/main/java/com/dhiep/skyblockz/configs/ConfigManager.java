package com.dhiep.skyblockz.configs;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.utils.ChatUtils;
import com.dhiep.skyblockz.utils.FileUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static File dataFolder;
    private static HashMap<String, File> configFiles = new HashMap<>();
    private static HashMap<String, FileConfiguration> configs = new HashMap<>();

    public static void init(SkyblockZ instance) {
        dataFolder = instance.getDataFolder();
        configFiles.put("config.yml", new File(dataFolder, "config.yml"));

        checkFirstTime(instance);

        configs.put("config.yml", new YamlConfiguration());

        loadAllConfigs();
    }

    public static void loadAllConfigs() {
        try {
            for (Map.Entry<String, FileConfiguration> en : configs.entrySet()) {
                String name = en.getKey();
                FileConfiguration config = en.getValue();
                config.load(configFiles.get(name));
            }
        } catch (IOException | InvalidConfigurationException ex) {
            ChatUtils.warnConsole("Failed to load config files!");
            ex.printStackTrace();
        }

        //TODO config class
    }

    public static void saveAllConfigs() {
        try {
            for (Map.Entry<String, FileConfiguration> en : configs.entrySet()) {
                String name = en.getKey();
                FileConfiguration config = en.getValue();
                config.save(configFiles.get(name));
            }
        } catch (IOException ex) {
            ChatUtils.warnConsole("Failed to save config files!");
            ex.printStackTrace();
        }
    }

    public static boolean saveConfig(String which) {
        try {
            FileConfiguration config = configs.get(which);
            File configFile = configFiles.get(which);
            config.save(configFile);
        } catch (IOException e) {
            ChatUtils.warnConsole("Failed to save config: " + which);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void checkFirstTime(SkyblockZ instance) {
        try {
            //Create data folder
            if (!dataFolder.exists() && !dataFolder.mkdirs()) throw new IOException();

            //Create config files
            for (Map.Entry<String, File> en : configFiles.entrySet()) {
                if (!en.getValue().exists()) {
                    FileUtils.copy(instance.getResource(en.getKey()), en.getValue());
                }
            }
        } catch (IOException ex) {
            ChatUtils.severeConsole("Failed to create config files!");
            ex.printStackTrace();
        }
    }
}
