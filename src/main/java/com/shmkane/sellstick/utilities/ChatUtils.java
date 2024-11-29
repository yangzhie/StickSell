package com.shmkane.sellstick.utilities;

import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.SellStick;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class ChatUtils {

    // Send Messages
    public static void sendMsg(CommandSender sender, String string, boolean showPrefix) {
        Component msg = MiniMessage.miniMessage()
                .deserialize((showPrefix) ? SellstickConfig.prefix.concat(string) : string);
        if (sender instanceof ConsoleCommandSender)
            log(Level.INFO, string);
        else if (sender instanceof Player)
            sender.sendMessage(msg);
    }

    // Send Action Bar Messages
    public static void sendActionBar(CommandSender sender, String string) {
        Component msg = MiniMessage.miniMessage().deserialize(string);
        sender.sendActionBar(msg);
    }

    public static void sendCommandNotProperMessage(CommandSender sender) {
        if (sender.hasPermission("sellstick.give")) {
            ChatUtils.sendMsg(sender, NamedTextColor.GREEN + "/SellStick give <player> <amount> (<uses>/infinite)",
                    true);
        }
        if (sender.hasPermission("sellstick.reload")) {
            ChatUtils.sendMsg(sender, NamedTextColor.GREEN + "/SellStick reload", true);
        }
    }

    // Server Logger
    public static void log(Level level, String string) {
        SellStick.getInstance().getLogger().log(level, string);
    }
}
