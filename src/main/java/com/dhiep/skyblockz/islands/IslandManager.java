package com.dhiep.skyblockz.islands;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.database.IslandData;
import com.dhiep.skyblockz.database.PlayerData;
import com.dhiep.skyblockz.models.SkyblockIsland;
import com.dhiep.skyblockz.models.SkyblockPlayer;
import com.dhiep.skyblockz.slimeworld.SlimeWorldManager;
import com.dhiep.skyblockz.utils.ChatUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IslandManager {
    private static List<String> pendingPlayers = new ArrayList<>();

    public static void createIsland(Player player) {
        SkyblockPlayer skyblockPlayer = PlayerData.getPlayerData(player);

        //Player already have island
        if (skyblockPlayer.getIslandUUID() != null) {
            ChatUtils.sendPlayerMessage(player, true,
                    "&cBạn đã có đảo rồi! Hãy về đảo bằng lệnh &7/island home");
            return;
        }

        SlimeWorldManager.createWorld(player);
    }

    public static void goHome(Player player) {
        SkyblockPlayer skyblockPlayer = PlayerData.getPlayerData(player);
        String islandUUID = skyblockPlayer.getIslandUUID();

        //Player doesnt have island
        if (islandUUID == null) {
            ChatUtils.sendPlayerMessage(player, true,
                    "&cBạn chưa có đảo! Hãy tạo đảo mới hoặc chơi chung với người khác.");
            return;
        }

        //Cannot load island
        SkyblockIsland skyblockIsland = IslandData.getIsland(islandUUID);
        if (skyblockIsland == null) {
            ChatUtils.sendPlayerMessage(player, true,
                    "&cĐã có lỗi xảy ra khi tải đảo của bạn! Hãy liên hệ với Staff nếu bạn không thể về đảo.");
            return;
        }

        SlimeWorldManager.loadWorld(player, skyblockPlayer, skyblockIsland);
    }

    public static void deleteIsland(Player player) {
        String islandUUID = PlayerData.getPlayerData(player).getIslandUUID();

        //Player doesnt have island
        if (islandUUID == null) {
            ChatUtils.sendPlayerMessage(player, true,
                    "&cBạn chưa có đảo! Hãy tạo đảo mới hoặc chơi chung với người khác.");
            return;
        }

        //Player is not island owner
        if (!islandUUID.equals(player.getUniqueId().toString())) {
            ChatUtils.sendPlayerMessage(player, true, "&cChỉ có chủ đảo mới có thể xóa đảo!");
            return;
        }

        String name = player.getName();
        //Check pending
        if (pendingPlayers.contains(name)) {
            ChatUtils.sendPlayerMessage(player, true, "&cBạn đang có yêu cầu xóa đảo đang chờ!");
            return;
        }

        //Confirm action
        player.spigot().sendMessage((new ComponentBuilder(ChatUtils.getLineBreak(0))
                .append("Bạn có chắc chắn là muốn xóa đảo của mình không? " +
                        "Bạn không thể hoàn tác hành động này và Staff cũng không thể giúp bạn!\n").color(ChatColor.YELLOW)
                .append("[XÁC NHẬN]").color(ChatColor.GREEN).bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("/is confirm", ChatColor.GRAY)))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is confirm")).append(" ").reset()
                .append("[HỦY BỎ]").color(ChatColor.RED).bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("/is cancel", ChatColor.GRAY)))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/is cancel")).append(" ").reset()
                .append(TextComponent.fromLegacyText(ChatUtils.getLineBreak(1)))
                .create()));

        pendingPlayers.add(name);

        //10s to confirm
        SkyblockZ.newChain().delay(20*10).sync(() -> {
            if (!pendingPlayers.remove(name)) return;
            ChatUtils.sendPlayerMessage(player, false, true, "&cYêu cầu xóa đảo của bạn đã hết hạn!");
        }).execute();
    }

    public static void confirmPending(Player player) {
        String name = player.getName();
        if (!pendingPlayers.remove(name)) {
            ChatUtils.sendPlayerMessage(player, true, "&cBạn không có yêu cầu nào đang chờ!");
            return;
        }
        SlimeWorldManager.deleteWorld(player);
    }

    public static void cancelPending(Player player) {
        String name = player.getName();
        if (!pendingPlayers.remove(name)) {
            ChatUtils.sendPlayerMessage(player, true, "&cBạn không có yêu cầu nào đang chờ!");
            return;
        }
        ChatUtils.sendPlayerMessage(player, true, "&cĐã hủy yêu cầu xóa đảo của bạn!");
    }

    public static void unloadIsland(Player player) {
        String islandUUID = PlayerData.getPlayerData(player).getIslandUUID();
        //Player doesnt have island
        if (islandUUID == null) return;

        SkyblockIsland skyblockIsland = IslandData.getIsland(islandUUID);
        //Check online owner and cancel unload
        if (Bukkit.getPlayer(UUID.fromString(skyblockIsland.getOwnerUUID())) != null) return;
        //Check online member and cancel unload
        for (String memberUUID : skyblockIsland.getMembers()) {
            if (Bukkit.getPlayer(UUID.fromString(memberUUID)) != null) return;
        }
        SlimeWorldManager.unloadWorld(islandUUID);
    }
}
