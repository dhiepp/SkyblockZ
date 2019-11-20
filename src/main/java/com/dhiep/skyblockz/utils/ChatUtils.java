package com.dhiep.skyblockz.utils;

import com.dhiep.skyblockz.SkyblockZ;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtils {
    private static String LINE_BREAK_FIRST = "&6--------------------------------------------------&r\n";
    private static String LINE_BREAK_LAST = "\n&6--------------------------------------------------";

    public static String getLineBreak(int fl) {
        if (fl == 0) return ChatColor.translateAlternateColorCodes('&', LINE_BREAK_FIRST);
        return ChatColor.translateAlternateColorCodes('&', LINE_BREAK_LAST);
    }

    public static void sendPlayerMessage(Player player, boolean addPrefix, String message) {
        if (player == null || !player.isOnline()) return;
        if (addPrefix) message = "&8[&e&lZ&8]&r " + message;
        message = ChatColor.translateAlternateColorCodes('&', message);
        player.sendMessage(message);
    }

    public static void sendPlayerMessage(Player player, boolean addPrefix, boolean addLineBreak, String message) {
        if (player == null || !player.isOnline()) return;
        if (addPrefix) message = "&8[&e&lZ&8]&r " + message;
        if (addLineBreak) message = LINE_BREAK_FIRST + message + LINE_BREAK_LAST;
        message = ChatColor.translateAlternateColorCodes('&', message);
        player.sendMessage(message);
    }

    public static void sendCommandSenderMessage(CommandSender sender, boolean addPrefix, String message) {
        if (addPrefix) message = "&8[&e&lZ&8]&r " + message;
        message = ChatColor.translateAlternateColorCodes('&', message);
        sender.sendMessage(message);
    }

    public static void infoConsole(String message) {
        SkyblockZ.getInstance().getLogger().info(message);
    }

    public static void warnConsole(String message) {
        SkyblockZ.getInstance().getLogger().warning(message);
    }

    public static void severeConsole(String message) {
        SkyblockZ.getInstance().getLogger().severe(message);
    }
}
