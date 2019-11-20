package com.dhiep.skyblockz.database;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.utils.ChatUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {
    private static final String DATABASE_NAME = "data.db";

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                SkyblockZ instance = SkyblockZ.getInstance();
                File dataFolder = instance.getDataFolder();
                File dataFile = new File(dataFolder + "/" + DATABASE_NAME);

                Class.forName("org.sqlite.JDBC");
                final String url = "jdbc:sqlite:" + dataFile;

                connection = DriverManager.getConnection(url);
                //Turn on Foreign Key support
                connection.prepareStatement("PRAGMA foreign_keys = ON").execute();
                //Create table if not exists
                createTables(connection);
            }
        } catch (Exception ex) {
            ChatUtils.severeConsole("Cannot load data file!");
            ex.printStackTrace();
        }
        return connection;
    }

    public static void close() {
        try {
            if (!connection.isClosed()) connection.close();
        } catch (SQLException ex) {
            ChatUtils.severeConsole("Cannot close data file!");
            ex.printStackTrace();
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS islands " +
                "(uuid TEXT not null primary key, team_size INTEGER default 4, radius INTEGER default 64, " +
                "spawnX INTEGER, spawnY INTEGER, spawnZ INTEGER)").execute();
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS players " +
                "(uuid TEXT not null primary key, name TEXT collate nocase, " +
                "island_uuid TEXT references islands on update cascade on delete set null, " +
                "spawnX INTEGER, spawnY INTEGER, spawnZ INTEGER);").execute();
    }
}
