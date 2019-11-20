package com.dhiep.skyblockz.teams;

import com.dhiep.skyblockz.database.IslandData;
import com.dhiep.skyblockz.database.PlayerData;
import com.dhiep.skyblockz.models.SkyblockIsland;
import com.dhiep.skyblockz.models.SkyblockPlayer;
import com.dhiep.skyblockz.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CoopManager {
    public static boolean addMember(String islandUUID, Player member) {
        SkyblockIsland skyblockIsland = IslandData.getIsland(islandUUID);
        if (skyblockIsland == null) return false;

        skyblockIsland.getMembers().put(member.getUniqueId().toString(), member.getName());
        SkyblockPlayer skyblockPlayer = PlayerData.getPlayerData(member);
        skyblockPlayer.setIslandUUID(islandUUID);
        skyblockPlayer.setSpawn(skyblockIsland.getSpawn());
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
        if (owner.getName().equals(member)) {
            ChatUtils.sendPlayerMessage(owner, true, "&cBạn không thể kick chính mình! " +
                    "&7Hãy chuyển quyền chủ đảo cho người khác trước!");
            return;
        }

        SkyblockIsland skyblockIsland = IslandData.getIsland(islandUUID);

        Player kicked = Bukkit.getPlayer(member);
        //Online kick
        if (kicked != null) {
            SkyblockPlayer kickedSkyblockPlayer = PlayerData.getPlayerData(kicked);
            if (!kickedSkyblockPlayer.getIslandUUID().equals(islandUUID)) {
                ChatUtils.sendPlayerMessage(owner, true,
                        "&cNgười chơi này không phải thành viên đảo bạn: &7" + kicked.getDisplayName());
                return;
            }
            kickedSkyblockPlayer.setIslandUUID(null);
            kicked.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());

            //Notify everyone
            ChatUtils.sendPlayerMessage(kicked, false, true,
                    "&cBạn đã bị xóa khỏi đảo của: &7" + owner.getDisplayName());
            ChatUtils.sendPlayerMessage(owner, false, true,
                    "&aĐã xóa thành viên khỏi đảo: &7" + kicked.getDisplayName());
            for (String mUUID : skyblockIsland.getMembers().keySet()) {
                Player m = Bukkit.getPlayer(UUID.fromString(mUUID));
                if (m !=null) {
                    ChatUtils.sendPlayerMessage(owner, false, true,
                            "&7" + kicked.getDisplayName() + " &cđã bị xóa khỏi đảo!");
                }
            }
        }
        else {
            PlayerData.removeOfflinePlayerIsland(owner, member);
        }
    }

    public static void memberLeave(Player member) {
        SkyblockPlayer skyblockPlayer = PlayerData.getPlayerData(member);
        String islandUUID = skyblockPlayer.getIslandUUID();
        String memberUUID = member.getUniqueId().toString();
        if (islandUUID == null) {
            ChatUtils.sendPlayerMessage(member, true, "&cBạn không có đảo!");
            return;
        }
        if (memberUUID.equals(islandUUID)) {
            ChatUtils.sendPlayerMessage(member, true, "&cBạn không thể rời đảo khi là chủ đảo! " +
                    "&7Hãy chuyển quyền chủ đảo cho người khác trước !");
            return;
        }

        SkyblockIsland island = IslandData.getIsland(islandUUID);

        HashMap<String, String> islandMembers = island.getMembers();
        if (islandMembers.remove(memberUUID) != null) {
            skyblockPlayer.setIslandUUID(null);

            member.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            ChatUtils.sendPlayerMessage(member, true, "&aBạn đã rời khỏi đảo thành công!");

            //Notify everyone
            Player owner = Bukkit.getPlayer(UUID.fromString(islandUUID));
            if (owner !=null) {
                ChatUtils.sendPlayerMessage(owner, false, true,
                        "&7" + member.getDisplayName() + " &cđã rời khỏi đảo!");
            }
            for (String mUUID : islandMembers.keySet()) {
                Player m = Bukkit.getPlayer(UUID.fromString(mUUID));
                if (m !=null) {
                    ChatUtils.sendPlayerMessage(owner, false, true,
                            "&7" + member.getDisplayName() + " &cđã rời khỏi đảo!");
                }
            }
        }
        else {
            ChatUtils.sendPlayerMessage(member, true, "&cĐã có lỗi xảy ra khi rời đảo!");
        }
    }
}
