package com.dhiep.skyblockz.teams;

import com.dhiep.skyblockz.database.IslandData;
import com.dhiep.skyblockz.database.PlayerData;
import com.dhiep.skyblockz.models.SkyblockIsland;
import com.dhiep.skyblockz.models.SkyblockPlayer;
import com.dhiep.skyblockz.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CoopManager {
    public static boolean addMember(String islandUUID, Player member) {
        SkyblockIsland skyblockIsland = IslandData.getIsland(islandUUID);
        if (skyblockIsland == null) return false;

        skyblockIsland.getMembers().add(member.getUniqueId().toString());
        PlayerData.getPlayerData(member).setIslandUUID(islandUUID);
        return true;
    }

    public static void removeMember(Player owner, String member) {
        String islandUUID = PlayerData.getPlayerData(owner).getIslandUUID();
        if (islandUUID == null) {
            ChatUtils.sendPlayerMessage(owner, true, "&cBạn không có đảo!");
            return;
        }
        if (!islandUUID.equals(owner.getUniqueId().toString())) {
            ChatUtils.sendPlayerMessage(owner, true, "&cBạn không phải là chủ đảo!");
            return;
        }

        SkyblockIsland skyblockIsland = IslandData.getIsland(islandUUID);

        if (member.equalsIgnoreCase(owner.getName())) {
            ChatUtils.sendPlayerMessage(owner, true, "&cBạn không thể tự kick chính mình! " +
                    "&7Hãy chuyển quyền chủ đảo cho người khác.");
            return;
        }

        ArrayList<String> islandMembers = skyblockIsland.getMembers();
        if (islandMembers.remove(member)) {
            OfflinePlayer kicked = Bukkit.getOfflinePlayer(member);

            //Online Kick
            if (kicked.isOnline()) {
                Player victim = kicked.getPlayer();
                PlayerData.getPlayerData(victim).setIslandUUID(null);
                victim.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                ChatUtils.sendPlayerMessage(victim, true, "&cBạn đã bị xóa khỏi đảo của: &7" + owner.getDisplayName());
                ChatUtils.sendPlayerMessage(owner, true, "&aĐã xóa thành viên khỏi đảo: &7" + victim.getDisplayName());
            }
            //Offline kick
            else {
                PlayerData.removeOfflinePlayerIsland(owner, kicked.getUniqueId().toString());
            }
        }
        else {
            ChatUtils.sendPlayerMessage(owner, true, "&cNgười chơi này không phải thành viên của đảo: &7" + member);
        }
    }

    public static void memberLeave(Player member) {
        SkyblockPlayer skyblockPlayer = PlayerData.getPlayerData(member);
        String islandUUID = skyblockPlayer.getIslandUUID();
        if (islandUUID == null) {
            ChatUtils.sendPlayerMessage(member, true, "&cBạn không có đảo!");
            return;
        }
        SkyblockIsland island = IslandData.getIsland(islandUUID);

        ArrayList<String> islandMembers = island.getMembers();
        if (islandMembers.remove(member.getUniqueId().toString())) {
            skyblockPlayer.setIslandUUID(null);

            member.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            ChatUtils.sendPlayerMessage(member, true, "&aBạn đã rời đảo!");
        }
        else {
            ChatUtils.sendPlayerMessage(member, true, "&cĐã có lỗi xảy ra khi rời đảo!");
        }
    }
}
