package com.shmkane.sellstick.utilities;

import com.shmkane.sellstick.configs.SellstickConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.logging.Level;

public class CommandUtils {

    public static void giveSellStick(Player target, int uses) {

        ItemStack itemStack;

        try {
            itemStack = new ItemStack(SellstickConfig.material);
        } catch (Exception ex) {
            ChatUtils.log(Level.SEVERE, SellstickConfig.prefix + " - Invalid item set in config. Please read the links I put in the config to fix this.");
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set display name
        itemMeta.displayName(MiniMessage.miniMessage().deserialize(SellstickConfig.displayName));

        // Apply meta to item stack
        itemStack.setItemMeta(itemMeta);

        // Add glow if required
        if (SellstickConfig.glow) {
            ItemUtils.glow(itemStack);
        }


        // Set NBT, uses and lore
        ItemUtils.setSellStick(itemStack);
        ItemStack finalItem = ItemUtils.setUses(itemStack, uses);

        // Remove any enchantments (if any)
        itemStack.removeEnchantment(Enchantment.FORTUNE);

        // Add to inventory
        target.getInventory().addItem(finalItem);
    }
}
