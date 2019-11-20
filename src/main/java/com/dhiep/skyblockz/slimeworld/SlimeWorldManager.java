package com.dhiep.skyblockz.slimeworld;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.database.IslandData;
import com.dhiep.skyblockz.database.PlayerData;
import com.dhiep.skyblockz.models.SkyblockIsland;
import com.dhiep.skyblockz.models.SkyblockPlayer;
import com.dhiep.skyblockz.utils.ChatUtils;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class SlimeWorldManager {
    private static SlimePlugin slimePlugin;
    private static SlimeLoader slimeLoader;
    private static SlimePropertyMap defaultPropertyMap;

    private static HashMap<Player, SkyblockIsland> waitingPlayers = new HashMap<>();

    public static void init() {
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        slimeLoader = slimePlugin.getLoader("file");
        defaultPropertyMap = new SlimePropertyMap();
        defaultPropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, true);
        defaultPropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, true);
        defaultPropertyMap.setBoolean(SlimeProperties.PVP, false);
        Location spawn = SkyblockIsland.getDefaultSpawn();
        defaultPropertyMap.setInt(SlimeProperties.SPAWN_X, spawn.getBlockX());
        defaultPropertyMap.setInt(SlimeProperties.SPAWN_Y, spawn.getBlockY());
        defaultPropertyMap.setInt(SlimeProperties.SPAWN_Z, spawn.getBlockZ());
        defaultPropertyMap.setString(SlimeProperties.DIFFICULTY, "normal");
        defaultPropertyMap.setString(SlimeProperties.ENVIRONMENT, "normal");
        defaultPropertyMap.setString(SlimeProperties.WORLD_TYPE, "flat");
    }

    private static boolean checkPending(Player player) {
        if (waitingPlayers.containsKey(player)) {
            ChatUtils.sendPlayerMessage(player, true, "&cĐảo của bạn đang được xử lý, hãy thử lại sau!");
            return true;
        }
        return false;
    }

    public static void createWorld(Player player) {
        if (checkPending(player)) return;
        String playerUUID = player.getUniqueId().toString();

        //Run create task
        SkyblockZ.newChain().asyncFirst(() -> {
            try {
                //Copy File
                File template = new File(Bukkit.getWorldContainer(), "/slime_worlds/static/SkyblockTemplate.slime");
                File newFile = new File(Bukkit.getWorldContainer(), "/slime_worlds/" + playerUUID + ".slime");
                Files.copy(template.toPath(), newFile.toPath());

                SlimeWorld slimeWorld = slimePlugin.loadWorld(slimeLoader, playerUUID, false, defaultPropertyMap);

                //Create config
                SkyblockPlayer skyblockPlayer = PlayerData.getPlayerData(player);
                skyblockPlayer.setIslandUUID(playerUUID);
                skyblockPlayer.setSpawn(SkyblockIsland.getDefaultSpawn());
                IslandData.createIsland(playerUUID);

                return slimeWorld;
            } catch (Exception ex) {
                ChatUtils.severeConsole("Failed to create new Slime World for player " + player.getName());
                ChatUtils.sendPlayerMessage(player, true,
                        "&cĐã có lỗi xảy ra khi tạo đảo mới cho bạn! Hãy liên hệ với Staff nếu bạn không thể tạo đảo.");
                ex.printStackTrace();
            }
            return null;
        }).abortIfNull().syncLast((slimeWorld) -> {
            slimePlugin.generateWorld(slimeWorld);

            ChatUtils.infoConsole("Creating new Slime World for player " + player.getName());
            ChatUtils.sendPlayerMessage(player, true,"&bĐang tạo đảo mới cho bạn...");
            waitingPlayers.put(player, null);
        }).execute();
    }

    public static void loadWorld(Player player, SkyblockPlayer skyblockPlayer, SkyblockIsland skyblockIsland) {
        if (checkPending(player)) return;
        String islandUUID = skyblockIsland.getOwnerUUID();

        //Check loaded
        World check = Bukkit.getWorld(islandUUID);
        if (check != null) {
            Location spawn = skyblockPlayer.getSpawn();
            if (spawn == null) spawn = skyblockIsland.getSpawn();
            spawn.setWorld(check);
            ChatUtils.sendPlayerMessage(player,true, "&aĐã về đảo của bạn");
            player.teleport(spawn);
            return;
        }

        //Run load task
        SkyblockZ.newChain().asyncFirst(() -> {
            try {
                SlimePropertyMap props = new SlimePropertyMap();
                props.merge(defaultPropertyMap);
                Location spawn = skyblockIsland.getSpawn();
                props.setInt(SlimeProperties.SPAWN_X, spawn.getBlockX());
                props.setInt(SlimeProperties.SPAWN_Y, spawn.getBlockY());
                props.setInt(SlimeProperties.SPAWN_Z, spawn.getBlockZ());

                return slimePlugin.loadWorld(slimeLoader, islandUUID, false, props);
            } catch (Exception ex) {
                ChatUtils.sendPlayerMessage(player, true,
                        "&cĐã có lỗi xảy ra khi tải đảo của bạn! Hãy liên hệ với Staff nếu bạn không thể về đảo.");
                ChatUtils.warnConsole("Failed to load Slime World for player " + player.getName());
                ex.printStackTrace();
            }
            return null;
        }).abortIfNull().syncLast((world) -> {
            slimePlugin.generateWorld(world);

            ChatUtils.sendPlayerMessage(player, true,"&bĐang tải đảo của bạn...");
            waitingPlayers.put(player, skyblockIsland);
        }).execute();
    }

    public static void worldLoadProcess(World world) {
        for (Map.Entry<Player, SkyblockIsland> en: waitingPlayers.entrySet()) {
            Player player = en.getKey();
            SkyblockIsland skyblockIsland = en.getValue();

            //Create new island
            if (skyblockIsland == null && player.getUniqueId().toString().equals(world.getName())) {
                waitingPlayers.remove(player);

                world.getWorldBorder().setSize(SkyblockIsland.getDefaultSize());

                Location spawn = SkyblockIsland.getDefaultSpawn();
                spawn.setWorld(world);
                player.teleport(spawn);
                ChatUtils.sendPlayerMessage(player,true, "&aĐã về đảo của bạn");
                return;
            }

            //Load existing island
            if (skyblockIsland != null && skyblockIsland.getOwnerUUID().equals(world.getName())) {
                waitingPlayers.remove(player);

                world.getWorldBorder().setSize(skyblockIsland.getRadius());
                Location spawn = skyblockIsland.getSpawn();
                spawn.setWorld(world);
                player.teleport(spawn);
                ChatUtils.sendPlayerMessage(player,true, "&aĐã về đảo của bạn");
                return;
            }
        }
    }

    public static void unloadWorld(String islandUUID) {
        if (Bukkit.unloadWorld(islandUUID, true)) {

            ChatUtils.debug("unloaded world " + islandUUID);
        } else {

            ChatUtils.debug("NOT unloaded world " + islandUUID);
        }
        IslandData.removeIsland(islandUUID);
    }

    public static void deleteWorld(Player player) {
        if (checkPending(player)) return;
        String islandUUID = player.getUniqueId().toString();
        SkyblockIsland skyblockIsland = IslandData.getIsland(islandUUID);
        List<Player> onlineMembers = new ArrayList<>();

        //Get online members
        for (String memberUUID : skyblockIsland.getMembers().keySet()) {
            Player member = Bukkit.getPlayer(UUID.fromString(memberUUID));
            if (member!=null) onlineMembers.add(member);
        }

        //Check loaded
        World check = Bukkit.getWorld(islandUUID);
        if (check != null) {
            //Teleport everyone to spawn
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            ChatUtils.sendPlayerMessage(player, true, "&aBạn đã được chuyển đi nơi khác để xóa đảo");
            for (Player member : onlineMembers) {
                ChatUtils.sendPlayerMessage(member, true, "&aBạn đã được chuyển đi nơi khác để xóa đảo");
                member.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            }
            //Unload world without saving
            Bukkit.unloadWorld(islandUUID, false);
        }

        ChatUtils.sendPlayerMessage(player, true,"&bĐang xóa đảo của bạn...");
        waitingPlayers.put(player, null);

        SkyblockZ.newChain().asyncFirst(() -> {
            File file = new File(Bukkit.getWorldContainer(), "/slime_worlds/" + islandUUID + ".slime");

            //Try to delete file every second for 5 times
            for (int i = 0; i < 5; i++) {
                try {
                    //If File has been deleted successfully
                    if (file.delete()) {
                        //Delete island in players' buffer
                        PlayerData.removeIslandInBuffer(player);
                        for (Player member : onlineMembers) {
                            PlayerData.removeIslandInBuffer(member);
                        }
                        //Delete island, will delete offline player's island too! (ON DELETE SET NULL)
                        IslandData.deleteIsland(player, islandUUID);
                        return true;
                    }

                    //If file cannot be deleted wait another second
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return false;
                }
            }

            //File still hasn't been deleted successfully
            return false;
        }).syncLast((success) -> {
            waitingPlayers.remove(player);
            if (success) {
                ChatUtils.sendPlayerMessage(player, true,"&aĐã xóa đảo của bạn thành công!");
            } else {
                ChatUtils.sendPlayerMessage(player, true,
                        "&cĐã có lỗi xảy ra khi xóa đảo của bạn! Hãy liên hệ Staff nếu bạn không thể xóa đảo của mình.");
                ChatUtils.warnConsole("Failed to delete Slime World for player " + player.getName());
            }
        }).execute();
    }
}
