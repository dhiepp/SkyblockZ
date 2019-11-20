package com.dhiep.skyblockz.commands;

import com.dhiep.skyblockz.islands.IslandManager;
import com.dhiep.skyblockz.utils.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            ChatUtils.sendCommandSenderMessage(sender, true, "&cThis command can only be used by a player!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("home")) {
            IslandManager.goHome(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("sethome")) {
            IslandManager.goHome(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            IslandManager.createIsland(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            IslandManager.deleteIsland(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("confirm")) {
            IslandManager.confirmPending(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("cancel")) {
            IslandManager.cancelPending(player);
            return true;
        }
        sendHelp(player);
        return true;
    }

    private static void sendHelp(Player player) {
        ChatUtils.sendPlayerMessage(player, false, true,"&7Hướng dẫn lệnh &eisland" +
                "\n&a/is &7-&f Mở menu đảo" +
                "\n&a/is home &7-&f Về đảo của bạn" +
                "\n&a/is sethome &7-&f Đặt nơi dịch chuyển về đảo" +
                "\n&dNhững câu lệnh của chủ đảo" +
                "\n&a/is create &7-&f Tạo đảo mới" +
                "\n&a/is delete &7-&f Xóa đảo của bạn" +
                "\n&a/is confirm &7-&f Xác nhận yêu cầu xóa đảo" +
                "\n&a/is cancel &7-&f Hủy yêu cầu xóa đảo");
    }
}
