package com.shmkane.sellstick.utilities;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

public class MergeUtils {
    // Search through player inventory to get all sellsticks
    public static ItemStack[] searchInventory(Player player) {
        // Get inventory
        Inventory inventory = player.getInventory();
        // List to store all sellsticks
        List<ItemStack> sellsticks = new ArrayList<>();

        // Search through inventory to find all sellsticks and store in sellsticks list
        for (ItemStack item : inventory.getContents()) {
            if (item != null && ItemUtils.matchSellStickUUID(item)) {
                int amount = item.getAmount();

                // Account for multiple sellsticks in same slot
                for (int i = 0; i < amount; i++) {
                    // Separates sellsticks in the same slot into individual items
                    ItemStack singleSellstick = item.clone();
                    singleSellstick.setAmount(1);
                    sellsticks.add(singleSellstick);
                }
            }
        }

        // Return sellsticks list as an array
        return sellsticks.toArray(new ItemStack[0]);
    }

    // Sort sellsticks by their uses
    public static ItemStack[] sortSellsticksByUses(ItemStack[] sellsticks) {
        List<ItemStack> sortedSellsticks = new ArrayList<>(List.of(sellsticks));

        sortedSellsticks.sort(Comparator.comparingInt(ItemUtils::getUses));

        return sortedSellsticks.toArray(new ItemStack[0]);
    }

    // Sum the uses of all sellsticks
    public static int sumSellStickUses(ItemStack[] sortedSellsticks, int maxAmount) {
        int usesSum = 0;

        // Sum the uses of all sellsticks in sellsticks array
        for (ItemStack sellstick : sortedSellsticks) {
            int uses = ItemUtils.getUses(sellstick);
            if (usesSum + uses <= maxAmount) {
                usesSum += uses;
            } else {
                break;
            }
        }

        return usesSum;
    }

    // Remove all sorted sellsticks from player inventory
    public static void removeSortedSellsticks(Player player, ItemStack[] sortedSellsticks, int maxAmount) {
        // Get inventory
        Inventory inventory = player.getInventory();
        int usesSum = 0;

        // Remove sellsticks which are less than maxAmount
        for (ItemStack sellstick : sortedSellsticks) {
            int uses = ItemUtils.getUses(sellstick);
            if (usesSum + uses <= maxAmount) {
                usesSum += uses; // Sum the uses before removing the sellstick

                for (ItemStack item : inventory.getContents()) {
                    if (item != null && item.isSimilar(sellstick)) {
                        int amount = item.getAmount();

                        // Handle multiple sellsticks in same slot
                        if (amount > 1) {
                            item.setAmount(amount - 1); // Decrement stack size
                        } else {
                            inventory.remove(item);
                        }
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    // TODO: Add algorithm to charge the user for merging sellsticks
}
