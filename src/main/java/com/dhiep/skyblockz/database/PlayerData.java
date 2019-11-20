package com.dhiep.skyblockz.database;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.models.SkyblockPlayer;
import com.dhiep.skyblockz.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    private static ConcurrentHashMap<String, SkyblockPlayer> playing = new ConcurrentHashMap<>();

    public static SkyblockPlayer getPlayerData(Player player) {
        return playing.get(player.getUniqueId().toString());
    }

    public static void removeIslandInBuffer(Player player) {
        SkyblockPlayer skyblockPlayer = getPlayerData(player);
        skyblockPlayer.setIslandUUID(null);
        skyblockPlayer.setSpawn(null);
    }

    public static void loadPlayer(Player player) {
        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                String playerName = player.getName();
                String playerUUID = player.getUniqueId().toString();
                Connection conn = DataConnection.getConnection();

                PreparedStatement sql = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?");
                sql.setString(1, playerUUID);

                ResultSet rs = sql.executeQuery();
                //No data yet
                if (!rs.next()) {
                    createPlayer(player);
                    return;
                }

                SkyblockPlayer skyblockPlayer = new SkyblockPlayer(playerName);
                String islandUUID = rs.getString("island_uuid");
                skyblockPlayer.setIslandUUID(islandUUID);
                int sX = rs.getInt("spawnX");
                int sY = rs.getInt("spawnY");
                int sZ = rs.getInt("spawnZ");
                Location spawn = null;
                if (sX!=0 && sY!=0 && sZ!=0) spawn = new Location(null, sX, sY, sZ);
                skyblockPlayer.setSpawn(spawn);

                playing.put(playerUUID, skyblockPlayer);

                //Also load the island of player (if not already loaded)
                if (islandUUID != null) {
                    IslandData.loadIsland(islandUUID);
                }
            } catch (SQLException ex) {
                ChatUtils.warnConsole("Cannot load data of player: " + player.getName());
                ex.printStackTrace();
            }
        }).execute();
    }

    public static void createPlayer(Player player) {
        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                String playerName = player.getName();
                String playerUUID = player.getUniqueId().toString();
                Connection conn = DataConnection.getConnection();
                SkyblockPlayer skyblockPlayer = new SkyblockPlayer(playerName);

                PreparedStatement sql = conn.prepareStatement("INSERT INTO players (uuid, name) VALUES (?, ?)");
                sql.setString(1, playerUUID);
                sql.setString(2, playerName);
                if (sql.executeUpdate() < 1) throw new SQLException();

                playing.put(playerUUID, skyblockPlayer);
            } catch (SQLException ex) {
                ChatUtils.warnConsole("Cannot create data of player: " + player.getName());
                ex.printStackTrace();
            }
        }).execute();
    }

    public static void savePlayerQuit(Player player) {
        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            try {
                String playerUUID = player.getUniqueId().toString();
                SkyblockPlayer skyblockPlayer = playing.remove(playerUUID);
                Connection conn = DataConnection.getConnection();
                savePlayerData(conn, playerUUID, skyblockPlayer);
            } catch (SQLException ex) {
                ChatUtils.warnConsole("Cannot save data of player: " + player.getName());
                ex.printStackTrace();
            }

        }).execute();
    }

    public static void saveOnlinePlayers() {
        SkyblockZ.newSharedChain("SQLITE").async(() -> {
            Connection conn = DataConnection.getConnection();
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    String playerUUID = player.getUniqueId().toString();
                    SkyblockPlayer skyblockPlayer = getPlayerData(player);
                    savePlayerData(conn, playerUUID, skyblockPlayer);
                } catch (SQLException ex) {
                    ChatUtils.warnConsole("Cannot save data of player: " + player.getName());
                    ex.printStackTrace();
                }
            }
        }).execute();
    }

    private static void savePlayerData(Connection conn, String playerUUID, SkyblockPlayer skyblockPlayer) throws SQLException {
        Location spawn = skyblockPlayer.getSpawn();

        PreparedStatement sql = conn.prepareStatement(
                "UPDATE players SET (island_uuid, spawnX, spawnY, spawnZ) = (?, ?, ?, ?) WHERE uuid = ?");
        sql.setString(1, skyblockPlayer.getIslandUUID());
        if (spawn == null) {
            sql.setNull(2, Types.INTEGER);
            sql.setNull(3, Types.INTEGER);
            sql.setNull(4, Types.INTEGER);
        } else {
            sql.setInt(2, spawn.getBlockX());
            sql.setInt(3, spawn.getBlockY());
            sql.setInt(4, spawn.getBlockZ());
        }
        sql.setString(5, playerUUID);

        if (sql.executeUpdate() < 1) throw new SQLException();
    }

    public static void removeOfflinePlayerIsland(Player owner, String playerUUID) {
        try {
            Connection conn = DataConnection.getConnection();

            PreparedStatement sql = conn.prepareStatement(
                    "UPDATE players SET island_uuid = null WHERE uuid = ?");
            sql.setString(1, playerUUID);

            if (sql.executeUpdate() < 1) throw new SQLException();
        } catch (SQLException ex) {
            ChatUtils.sendPlayerMessage(owner, true, "&cĐã có lỗi xảy ra khi kick thành viên này!");
            ChatUtils.warnConsole("Failed to remove offline player island: " + playerUUID);
            ex.printStackTrace();
        }
    }
}