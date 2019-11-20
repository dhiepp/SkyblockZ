package com.dhiep.skyblockz.database;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.models.SkyblockIsland;
import com.dhiep.skyblockz.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class IslandData {
    private static ConcurrentHashMap<String, SkyblockIsland> loadedIslands = new ConcurrentHashMap<>();

    public static SkyblockIsland getIsland(String islandUUID) {
        return loadedIslands.get(islandUUID);
    }

    public static void removeIsland(String islandUUID) {
        loadedIslands.remove(islandUUID);
    }

//    public static boolean checkIsland(String islandUUID) {
//        return loadedIslands.containsKey(islandUUID);
//    }

    //Load the island after load player data (when player join)
    public static void loadIsland(String islandUUID) {
        if (loadedIslands.containsKey(islandUUID)) return;

        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                Connection conn = DataConnection.getConnection();

                //GET basic island data
                PreparedStatement sql = conn.prepareStatement("SELECT * FROM islands WHERE uuid = ?");
                sql.setString(1, islandUUID);

                ResultSet rs = sql.executeQuery();
                //No data yet which is impossible because islandUUID is a parameter!
                if (!rs.next()) throw new SQLException();

                SkyblockIsland skyblockIsland = new SkyblockIsland(islandUUID);
                skyblockIsland.setTeamSize(rs.getInt("team_size"));
                skyblockIsland.setRadius(rs.getInt("radius"));
                Location spawn = new Location(null,
                        rs.getInt("spawnX"), rs.getInt("spawnY"), rs.getInt("spawnZ"));
                skyblockIsland.setSpawn(spawn);

                //GET members list
                sql = conn.prepareStatement("SELECT uuid FROM players WHERE island_uuid = ?");
                sql.setString(1, islandUUID);

                rs = sql.executeQuery();
                ArrayList<String> members = new ArrayList<>();
                while (!rs.next()) {
                    String memberUUID = rs.getString("puuid");
                    members.add(memberUUID);
                }
                skyblockIsland.setMembers(members);

                //Put the loaded island in buffer
                loadedIslands.put(islandUUID, skyblockIsland);
            } catch (SQLException ex) {
                ChatUtils.warnConsole("Cannot load data of island: " + islandUUID);
                ex.printStackTrace();
            }
        }).execute();
    }

    public static void createIsland(String islandUUID) {
        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                Connection conn = DataConnection.getConnection();

                SkyblockIsland skyblockIsland = SkyblockIsland.newDefaultIsland(islandUUID);

                PreparedStatement sql = conn.prepareStatement(
                        "INSERT INTO islands (uuid, team_size, radius, spawnX, spawnY, spawnZ) VALUES (?, ?, ?, ?, ?, ?)");
                sql.setString(1, islandUUID);
                sql.setInt(2, skyblockIsland.getTeamSize());
                sql.setInt(3, skyblockIsland.getRadius());
                Location spawn = skyblockIsland.getSpawn();
                sql.setInt(4, spawn.getBlockX());
                sql.setInt(5, spawn.getBlockY());
                sql.setInt(6, spawn.getBlockZ());

                if (sql.executeUpdate() < 1) throw new SQLException();

                loadedIslands.put(islandUUID, skyblockIsland);
            } catch (SQLException ex) {
                ChatUtils.warnConsole("Cannot create data of island: " + islandUUID);
                ex.printStackTrace();
            }
        }).execute();
    }

    public static void updateIsland(String islandUUID) {
        if (!loadedIslands.containsKey(islandUUID)) return;

        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                Connection conn = DataConnection.getConnection();
                SkyblockIsland skyblockIsland = loadedIslands.get(islandUUID);

                PreparedStatement sql = conn.prepareStatement(
                        "UPDATE islands SET (team_size, radius, spawnX, spawnY, spawnZ) = (?, ?, ?, ?, ?) WHERE uuid = ?");
                sql.setInt(1, skyblockIsland.getTeamSize());
                sql.setInt(2, skyblockIsland.getRadius());
                Location spawn = skyblockIsland.getSpawn();
                sql.setInt(3, spawn.getBlockX());
                sql.setInt(4, spawn.getBlockY());
                sql.setInt(5, spawn.getBlockZ());
                sql.setString(6, islandUUID);

                if (sql.executeUpdate() < 1) throw new SQLException();
            } catch (SQLException ex) {
                ChatUtils.warnConsole("Cannot update data of island: " + islandUUID);
                ex.printStackTrace();
            }
        }).execute();
    }

    public static void updateIslandOwner(String oldIslandUUID, String newIslandUUID) {
        if (!loadedIslands.containsKey(oldIslandUUID)) return;

        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                Connection conn = DataConnection.getConnection();

                PreparedStatement sql = conn.prepareStatement("UPDATE islands SET uuid = ? WHERE uuid = ?");
                sql.setString(1, newIslandUUID);
                sql.setString(2, oldIslandUUID);

                if (sql.executeUpdate() < 1) throw new SQLException();
                SkyblockIsland skyblockIsland = loadedIslands.get(oldIslandUUID);
                loadedIslands.remove(oldIslandUUID);
                loadedIslands.put(newIslandUUID, skyblockIsland);
            } catch (SQLException ex) {
                ChatUtils.warnConsole("Cannot update owner of island: " + oldIslandUUID);
                ex.printStackTrace();
            }
        }).execute();
    }

    public static void deleteIsland(Player player, String islandUUID) {
        if (!loadedIslands.containsKey(islandUUID)) return;

        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                Connection conn = DataConnection.getConnection();

                PreparedStatement sql = conn.prepareStatement("DELETE FROM islands WHERE uuid = ?");
                sql.setString(1, islandUUID);

                if (sql.executeUpdate() < 1) throw new SQLException();
                loadedIslands.remove(islandUUID);
            } catch (SQLException ex) {
                ChatUtils.sendPlayerMessage(player, true,
                    "&cĐã có lỗi xảy ra khi xóa đảo của bạn! Hãy liên hệ Staff nếu bạn không thể xóa đảo của mình.");
                ChatUtils.warnConsole("Cannot delete data of island: " + islandUUID);
                ex.printStackTrace();
            }
        }).execute();
    }
}
