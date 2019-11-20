package com.dhiep.skyblockz.configs;

@Deprecated
public class IslandStorage {
//    private static FileConfiguration islandConfig;
//    private static List<SkyblockIsland> islandList = new ArrayList<>();
//
//    public static void init(FileConfiguration islandConfig) {
//        IslandStorage.islandConfig = islandConfig;
//    }
//
//    public static SkyblockIsland loadIsland(String islandUUID) {
//        ConfigurationSection node = islandConfig.getConfigurationSection(islandUUID);
//
//        //Check exists
//        if (node == null) return null;
//
//        SkyblockIsland island = new SkyblockIsland(islandUUID);
//        island.setMembers(node.getStringList("members"));
//        island.setTeamSize(node.getInt("teamsize"));
//        island.setSize(node.getInt("size"));
//        island.setSpawnX(node.getInt("spawnX"));
//        island.setSpawnY(node.getInt("spawnY"));
//        island.setSpawnZ(node.getInt("spawnZ"));
//
//        playerIslandList.add(island);
//        return island;
//    }
//
//    public static boolean createIsland(String islandUUID) {
//        SkyblockIsland island = SkyblockIsland.newDefaultIsland(islandUUID);
//        ConfigurationSection node = islandConfig.createSection(islandUUID);
//
//        return setData(island, node);
//    }
//
//    public static boolean editIsland(SkyblockIsland island) {
//        ConfigurationSection node = islandConfig.getConfigurationSection(island.getUuid());
//
//        return setData(island, node);
//    }
//
//    private static boolean setData(SkyblockIsland island, ConfigurationSection node) {
//        node.set("members", island.getMembers());
//        node.set("teamsize", island.getTeamSize());
//        node.set("size", island.getSize());
//        node.set("spawnX", island.getSpawnX());
//        node.set("spawnY", island.getSpawnY());
//        node.set("spawnZ", island.getSpawnZ());
//
//        return ConfigManager.saveConfig("islands.yml");
//    }
//
//    public static boolean deleteIsland(String islandUUID) {
//        islandConfig.set(islandUUID, null);
//
//        return ConfigManager.saveConfig("islands.yml");
//    }
}