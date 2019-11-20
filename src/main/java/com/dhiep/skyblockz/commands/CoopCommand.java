package com.dhiep.skyblockz.commands;

import com.dhiep.skyblockz.teams.CoopManager;
import com.dhiep.skyblockz.teams.InviteManager;
import com.dhiep.skyblockz.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoopCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtils.sendCommandSenderMessage(sender, true, "&cThis command can only be used by a player!");
            return true;
        }

        Player player = (Player)sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("invite")) {
            if (args.length == 2) InviteManager.createInvite(player, args[1]);
            else sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length == 2) InviteManager.acceptInvite(player, args[1]);
            else sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("deny")) {
            if (args.length == 2) InviteManager.denyInvite(player, args[1]);
            else sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            if (args.length == 2) CoopManager.removeMember(player, args[1]);
            else sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            CoopManager.memberLeave(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("leader")) {
            ChatUtils.sendPlayerMessage(player, true, "&cTính năng này đang được xây dựng!");
            return true;
        }
        sendHelp(player);
        return true;
    }

    private static void sendHelp(Player player) {
        ChatUtils.sendPlayerMessage(player, false, true,"&7Hướng dẫn lệnh &ecoop" +
                "\n&a/coop accept <tên> &7-&f Chấp nhận lời mời" +
                "\n&a/coop deny <tên> &7-&f Từ chối lời mời" +
                "\n&a/coop leave &7-&f Rời khỏi đảo" +
                "\n&dNhững câu lệnh của chủ đảo" +
                "\n&a/coop invite <tên> &7-&f Mời thành viên vào đảo" +
                "\n&a/coop kick <tên> &7-&f Xóa thành viên khỏi đảo" +
                "\n&a/coop leader <tên> &7-&f Chuyển sở hữu đảo sang thành viên khác");
    }
}
