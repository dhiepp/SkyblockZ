package com.dhiep.skyblockz.models;

import org.bukkit.Location;

import java.util.ArrayList;

public class SkyblockIsland {
    private String ownerUUID;
    private ArrayList<String> members = new ArrayList<>();
    private int teamSize;
    private int radius;
    private Location spawn;

    public static SkyblockIsland newDefaultIsland(String islandUUID) {
        SkyblockIsland island = new SkyblockIsland(islandUUID);
        island.setMembers(new ArrayList<>());
        island.setTeamSize(4);
        island.setRadius(64);
        island.setSpawn(new Location(null, 8, 100, 3));
        return island;
    }

    public static int getDefaultTeamSize() { return 4;}
    public static int getDefaultSize() { return 64;}
    public static Location getDefaultSpawn() { return new Location(null, 8, 100, 3);}

    public SkyblockIsland() {}

    public SkyblockIsland(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public String getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(String ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}
