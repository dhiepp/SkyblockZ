package com.dhiep.skyblockz.teams;

import com.dhiep.skyblockz.SkyblockZ;
import com.dhiep.skyblockz.database.IslandData;
import com.dhiep.skyblockz.database.PlayerData;
import com.dhiep.skyblockz.models.SkyblockIsland;
import com.dhiep.skyblockz.slimeworld.SlimeWorldManager;
import com.dhiep.skyblockz.utils.ChatUtils;
import com.google.common.collect.HashMultimap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class InviteManager {
    //Target name and inviters
    private static HashMultimap<String, Player> pendingInvites = HashMultimap.create();
//    private static HashMap<String, List<Player>> pendingInvites = new HashMap<>();

    public static void createInvite(Player inviter, String rawTargetName) {
        SkyblockIsland skyblockIsland = IslandData.getIsland(inviter.getUniqueId().toString());

        if (skyblockIsland == null) {
            ChatUtils.sendPlayerMessage(inviter, true, "&cBạn không có đảo hoặc không phải chủ đảo!");
            return;
        }


        if(rawTargetName.equalsIgnoreCase(inviter.getName())) {
            ChatUtils.sendPlayerMessage(inviter, true, "&cBạn không thể mời chính mình!");
            return;
        }

        Player target = Bukkit.getPlayer(rawTargetName);
        //Not Online
        if (target == null) {
            ChatUtils.sendPlayerMessage(inviter, true, "&cNgười chơi không online: &7" + rawTargetName);
            return;
        }

        String targetName = target.getName();
        String targetDisplayName = target.getDisplayName();
        String inviterName = inviter.getName();
        String inviterDisplayName = inviter.getDisplayName();

        //Already have island
        if (PlayerData.getPlayerData(target).getIslandUUID() != null) {
            ChatUtils.sendPlayerMessage(inviter, true, "&cNgười chơi đã có đảo: &7" + targetDisplayName);
            return;
        }

        //Is pending
        if(pendingInvites.containsEntry(targetName, inviter)) {
            ChatUtils.sendPlayerMessage(inviter, true,
                    "&cNgười chơi đang có lời mời đang chờ từ bạn: &7" + targetDisplayName);
            return;
        }

        //Notice of invitation
        ChatUtils.sendPlayerMessage(inviter, false, true,
                "&eĐã gửi lời mời vào đảo đến &7" + targetDisplayName);

        //Inviting text
        target.spigot().sendMessage((new ComponentBuilder(ChatUtils.getLineBreak(0))
                .append(TextComponent.fromLegacyText(ChatColor.GRAY + inviter.getDisplayName()))
                .append(" đã mời bạn chơi chung đảo\n").color(ChatColor.YELLOW)
                .append("Lời mời sẽ hết hạn sau 60 giây.\n").color(ChatColor.YELLOW)
                .append("[ĐỒNG Ý]").color(ChatColor.GREEN).bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("/coop accept " + inviterName)))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/coop accept " + inviterName)).append(" ").reset()
                .append("[TỪ CHỐI]").color(ChatColor.RED).bold(true)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("/coop deny " + inviterName)))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/coop deny " + inviterName)).append(" ").reset()
                .append(TextComponent.fromLegacyText(ChatUtils.getLineBreak(1)))
                .create()));

        pendingInvites.put(targetName, inviter);

        SkyblockZ.newChain().delay(20*60).sync(() -> {
            //The invite is no more
            if (!pendingInvites.containsEntry(targetName, inviter)) return;

            //Remove the invite
            pendingInvites.remove(targetName, inviter);

            ChatUtils.sendPlayerMessage(inviter, false, true,
                    "&cLời mời chơi chung đến &7" + targetDisplayName + "&c đã hết hạn!");
            ChatUtils.sendPlayerMessage(target, false, true,
                    "&cLời mời chơi chung từ &7" + inviterDisplayName + "&c đã hết hạn!");
        }).execute();
    }

    public static void acceptInvite(Player member, String arg) {
        String memberName = member.getName();
        Player inviter = Bukkit.getPlayer(arg);

        //No invites
        if (pendingInvites.get(memberName).size() == 0) {
            ChatUtils.sendPlayerMessage(member, true, "&cBạn không có lời mời nào đang chờ!");
            return;
        }

        //Wrong name
        if (!pendingInvites.containsEntry(memberName, inviter)) {
            ChatUtils.sendPlayerMessage(member, true,
                    "&cBạn không có lời mời nào đang chờ từ &7" + arg);
            return;
        }

        if (inviter == null) {
            ChatUtils.sendPlayerMessage(member, true, "&cKhông thể chấp nhận lời mời do người mời đã offline!");
            return;
        }

        pendingInvites.remove(memberName, inviter);
        if (pendingInvites.get(memberName).size() == 0) pendingInvites.removeAll(memberName);
        String islandUUID = PlayerData.getPlayerData(inviter).getIslandUUID();

        if (CoopManager.addMember(islandUUID, member)) {
            ChatUtils.sendPlayerMessage(inviter, false, true,
                    "&7" + member.getDisplayName() + " &ađã chấp nhận lời mời!");
            ChatUtils.sendPlayerMessage(member, false, true,
                    "&aBạn đã là thành viên đảo của &7" + inviter.getDisplayName());
            SlimeWorldManager.loadWorld(member, PlayerData.getPlayerData(member), IslandData.getIsland(islandUUID));
        } else {
            ChatUtils.sendPlayerMessage(inviter, true, "&cĐã xảy ra lỗi khi thêm thành viên &7" + member.getDisplayName());
            ChatUtils.sendPlayerMessage(member, true, "&cĐã xảy ra lỗi khi vào đảo của &7" + inviter.getDisplayName());
        }
    }

    public static void denyInvite(Player member, String arg) {
        String memberName = member.getName();
        Player inviter = Bukkit.getPlayer(arg);

        //No invites
        if (pendingInvites.get(memberName).size() == 0) {
            ChatUtils.sendPlayerMessage(member, true, "&cBạn không có lời mời nào đang chờ!");
            return;
        }

        //Wrong name
        if (!pendingInvites.containsEntry(memberName, inviter)) {
            ChatUtils.sendPlayerMessage(member, true,
                    "&cBạn không có lời mời nào đang chờ từ &7" + arg);
            return;
        }

        if (inviter == null) return;

        pendingInvites.remove(memberName, inviter);
        if (pendingInvites.get(memberName).size() == 0) pendingInvites.removeAll(memberName);

        ChatUtils.sendPlayerMessage(inviter, false, true,
                "&7" + member.getDisplayName() + " &cđã từ chối lời mời của bạn!");
        ChatUtils.sendPlayerMessage(member, false, true,
                "&cĐã từ chối lời mời của &7" + inviter.getDisplayName());
    }
}
