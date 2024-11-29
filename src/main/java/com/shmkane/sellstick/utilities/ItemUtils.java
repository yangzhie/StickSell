package com.shmkane.sellstick.utilities;

import com.shmkane.sellstick.configs.SellstickConfig;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemUtils {

    private static final UUID uuid = UUID.fromString("c5faa888-4b14-11ee-be56-0242ac120002");

    // Make an ItemStack Glow
    public static ItemStack glow(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantment(Enchantment.FORTUNE, 1);
        return itemStack;
    }

    // Check if an ItemStack is infinite
    public static boolean isInfinite(ItemStack itemStack) {

        ReadableNBT nbtItemStack = NBT.readNbt(itemStack);

        return nbtItemStack.getBoolean("Infinite");
    }

    // Set an Itemstack with a NBT Tag of Infinite with a state
    public static ReadWriteNBT setInfinite(ItemStack itemStack) {

        ReadWriteNBT nbtItemStack = NBT.itemStackToNBT(itemStack);

        nbtItemStack.setBoolean("Infinite", true);
        nbtItemStack.setInteger("UsesRemaining", Integer.MAX_VALUE);

        return nbtItemStack;
    }

    // Get uses Remaining from a SellStick
    public static int getUses(ItemStack itemStack) {
        ReadableNBT nbtItemStack = NBT.readNbt(itemStack);
        return nbtItemStack.getInteger("UsesRemaining");
    }

    // Set uses to a SellStick
    public static ItemStack setUses(ItemStack itemStack, int uses) {

        // Update ItemMeta
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(setLoreList(uses));
        itemStack.setItemMeta(itemMeta);

        // NBT
        NBT.modify(itemStack, nbt -> {
            nbt.setInteger("UsesRemaining", uses);
            nbt.setBoolean("Infinite", (uses == Integer.MAX_VALUE));
        });

        return itemStack;
    }

    // Subtract a use from a SellStick
    public static ItemStack subtractUses(ItemStack itemStack) {

        // Skip if infinite
        if (isInfinite(itemStack)) return itemStack;

        // Subtract use
        int newUses = getUses(itemStack) - 1;
        NBT.modify(itemStack, nbt -> {
            nbt.setInteger("UsesRemaining", newUses);
        });

        // Update Uses on Lore
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.lore(setLoreList(newUses));
        itemStack.setItemMeta(itemMeta);
        return itemStack;

    }

    public static ItemStack setSellStick(ItemStack itemStack) {

        NBT.modify(itemStack, nbt -> {
            nbt.setString("SellStickUUID", uuid.toString());
            
            //nbt.setString("RandomSSUUID", UUID.randomUUID().toString()); // Make it non stackable
        });

        return itemStack;
    }

    @Deprecated
    public static boolean isSellStick(ItemStack itemStack) {
        boolean matchUUID = matchSellStickUUID(itemStack);
        boolean matchMaterial = matchSellStickMaterial(itemStack);

        return (matchUUID && matchMaterial);
    }

    public static boolean matchSellStickUUID(ItemStack itemStack) {
        ReadableNBT nbtItemStack = NBT.readNbt(itemStack);
        return nbtItemStack.getString("SellStickUUID").equals(uuid.toString());
    }

    public static boolean matchSellStickMaterial(ItemStack itemStack) {
        return itemStack.getType().equals(SellstickConfig.material);
    }

    public static List<Component> setLoreList(int uses){
        List<Component> loreList = new ArrayList<>();
        for(String loreLine : SellstickConfig.lore) {
            loreList.add(MiniMessage.miniMessage().deserialize(loreLine));
        }
        if(uses == Integer.MAX_VALUE){
            loreList.add(MiniMessage.miniMessage().deserialize(SellstickConfig.infiniteLore));
        } else {
            loreList.add(MiniMessage.miniMessage().deserialize(SellstickConfig.finiteLore.replace("%remaining%", String.valueOf(uses))));
        }
        return loreList;
    }

}
