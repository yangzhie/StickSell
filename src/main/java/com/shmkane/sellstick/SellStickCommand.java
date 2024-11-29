package com.shmkane.sellstick;

import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.utilities.ChatUtils;
import com.shmkane.sellstick.utilities.CommandUtils;
import com.shmkane.sellstick.utilities.ConvertUtils;
import com.shmkane.sellstick.utilities.ItemUtils;
import com.shmkane.sellstick.utilities.MergeUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SellStickCommand implements TabExecutor {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String[] args) {
        List<String> commands = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("sellstick.give")) {
                commands.add("give");
            }
            if (sender.hasPermission("sellstick.reload")) {
                commands.add("reload");
            }
            if (sender.hasPermission("sellstick.convert")) {
                commands.add("convert");
            }
            if (sender.hasPermission("sellstick.merge")) {
                commands.add("merge");
            }
        } else if (args.length == 2) {
            for (Player player : SellStick.getInstance().getServer().getOnlinePlayers()) {
                commands.add(player.getName());
            }
        } else if (args.length == 3) {
            commands.add("1");
        } else if (args.length == 4) {
            commands.add("i");
            commands.add("1");
            commands.add("2");
            commands.add("3");
            commands.add("5");
            commands.add("10");
        }
        return commands;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label,
            String[] args) {

        if (args.length == 0) {
            ChatUtils.sendCommandNotProperMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Reload Command
        if (subCommand.equals("reload") && sender.hasPermission("sellstick.reload")) {
            try {
                SellStick.getInstance().reload();
                return true;
            } catch (Exception ex) {
                ChatUtils.sendMsg(sender, "<red>Something went wrong! Check console for error", true);
                ChatUtils.log(Level.SEVERE, ex.getMessage());
                return false;
            }
        }

        // Convert Command
        if (subCommand.equals("convert") && sender.hasPermission("sellstick.convert")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return false;
            }
            ConvertUtils.convertSellStick((Player) sender);
            return true;
        }

        // Merge Command
        if (subCommand.equals("merge") && sender.hasPermission("sellstick.merge")) {
            // Get max amount of uses for a new sellstick
            int maxAmount = SellStick.getInstance().getMaxAmount();

            // Get player
            Player player = (Player) sender;

            // Get all sellsticks in player inventory
            ItemStack[] sellsticks = MergeUtils.searchInventory(player);

            // Check if player has any sellsticks
            if (sellsticks.length == 0) {
                ChatUtils.sendMsg(player, "<red>You have no sellsticks in your inventory!", true);
                return false;
            }

            // Check if player has at least 2 sellsticks
            if (sellsticks.length == 1) {
                ChatUtils.sendMsg(player, "<yellow>You need at least 2 sellsticks to merge!", true);
                return false;
            }

            // Sort sellsticks by their uses
            ItemStack[] sortedSellsticks = MergeUtils.sortSellsticksByUses(sellsticks);

            // Sum the uses of all sellsticks
            int usesSum = MergeUtils.sumSellStickUses(sortedSellsticks, maxAmount);

            // Remove all sellsticks from player inventory
            MergeUtils.removeSortedSellsticks(player, sortedSellsticks, maxAmount);

            // Give a new sellstick with a number of uses equalling usesSum
            CommandUtils.giveSellStick(player, usesSum);

            // Check if all sellsticks were merged
            int totalUsesBeforeMerge = 0;
            for (ItemStack sellstick : sortedSellsticks) {
                totalUsesBeforeMerge += ItemUtils.getUses(sellstick);
            }

            if (totalUsesBeforeMerge == usesSum) {
                ChatUtils.sendMsg(player, "<green>All sellsticks merged successfully!", true);
            } else if (totalUsesBeforeMerge > usesSum) {
                ChatUtils.sendMsg(player, "<red>Sellsticks exceed the maximum allowed merged uses.", true);
            }
        }

        // Toggle Command
        if (subCommand.equals("toggle") && sender.hasPermission("sellstick.toggle")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return false;
            }

            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            SellStick.togglePlayerPreference(playerUUID);

            boolean newPreference = SellStick.getPlayerPreference(playerUUID);
            String message = newPreference ? "Sell messages will now be sent in chat."
                    : "Sell messages will now be sent in the action bar.";
            ChatUtils.sendMsg(player, message, true);

            return true;
        }

        // Give Command
        else if (subCommand.equals("give") && sender.hasPermission("sellstick.give")) {

            if (args.length < 4) {
                ChatUtils.sendMsg(sender, "<red>Not enough arguments!", true);
                return false;
            }

            Player target = SellStick.getInstance().getServer().getPlayer(args[1]);
            if (target == null) {
                ChatUtils.sendMsg(sender, "<red>Player not found", true);
                return false;
            }

            // Check if the argument is an integer
            int numSticks;
            try {
                numSticks = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                ChatUtils.sendMsg(sender, "<red>Not a number: " + args[2], true);
                return false;
            }

            // Check if the stick is infinite
            String argUses = args[3].toLowerCase();
            int uses;

            if (argUses.equals("i") || argUses.equals("infinite")) {
                uses = Integer.MAX_VALUE;
            } else { // Parse the argument is a number
                try {
                    uses = Integer.parseInt(args[3]);
                } catch (NumberFormatException ex) {
                    ChatUtils.sendMsg(sender, "<red>Must be a number or 'i': ", true);
                    return false;
                }
            }

            // Give sell sticks
            for (int i = 0; i < numSticks; i++) {
                // TODO: Check if inventory is full or has enough slots??
                CommandUtils.giveSellStick(target, uses);
            }

            ChatUtils.sendMsg(target, SellstickConfig.receiveMessage.replace("%amount%", numSticks + ""), true);
            ChatUtils.sendMsg(sender, SellstickConfig.giveMessage.replace("%player%", target.getName())
                    .replace("%amount%", numSticks + ""), true);

            return true;
        } else {
            ChatUtils.sendMsg(sender, SellstickConfig.noPerm, true);
        }
        return false;
    }
}