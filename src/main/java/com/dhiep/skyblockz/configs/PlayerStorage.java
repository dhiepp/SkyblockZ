package com.dhiep.skyblockz.configs;

@Deprecated
public class PlayerStorage {
//    private static FileConfiguration playerConfig;
//
//    public static void init(FileConfiguration playerConfig) {
//        PlayerStorage.playerConfig = playerConfig;
//    }
//
//    public static String loadPlayerIsland(Player player) {
//        String playerUUID = player.getUniqueId().toString();
//        return playerConfig.getString(playerUUID + ".island");
//    }
//
//    public static boolean createPlayer(Player player, String islandUUID) {
//        String playerUUID = player.getUniqueId().toString();
//        ConfigurationSection node = playerConfig.createSection(playerUUID);
//
//        node.set("name", player.getName());
//        node.set("island", islandUUID);
//
//        return ConfigManager.saveConfig("players.yml");
//    }
//
//    public static boolean deletePlayer(String playerUUID) {
//        playerConfig.set(playerUUID, null);
//
//        return ConfigManager.saveConfig("players.yml");
//    }
}
