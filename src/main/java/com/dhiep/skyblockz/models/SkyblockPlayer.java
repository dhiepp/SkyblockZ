package com.dhiep.skyblockz.models;

import org.bukkit.Location;

public class SkyblockPlayer {
    private String name;
    private String islandUUID;
    private Location spawn;

    public SkyblockPlayer() {}

    public SkyblockPlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIslandUUID() {
        return islandUUID;
    }

    public void setIslandUUID(String islandUUID) {
        this.islandUUID = islandUUID;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}
