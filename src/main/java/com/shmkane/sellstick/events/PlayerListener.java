package com.shmkane.sellstick.events;

import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.utilities.*;

import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR) // Checks if other plugins are using the event
    public void onSellstickUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack sellStick = player.getInventory().getItemInMainHand();

        // Player preference for sell message
        boolean sendInChat = EventUtils.getPlayerPreference(player.getUniqueId());

        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return; // Must right-click
        if (sellStick.getItemMeta() == null || block == null)
            return; // Return if empty item

        // Convert old sellticks
        String name = player.getInventory().getItemInMainHand().getItemMeta().displayName().toString();
        if (name.startsWith("§e✦ §e§lSellStick") || name.startsWith("§6§lSellStick")) {
            ConvertUtils.convertSellStick(player);
            return;
        }

        // Replace unstackable sellstick with stackable one
        if (ConvertUtils.makeSellStickStackable(player, sellStick))
            return;

        if (event.getPlayer().isSneaking())
            return; // Check Player is not sneaking
        if (sellStick.getType().isAir())
            return; // Check if Item is air
        if (!ItemUtils.matchSellStickUUID(sellStick))
            return; // Check if Item has UUID of SellStick
        if (!EventUtils.didClickSellStickBlock(block))
            return; // Check if clicked block is a container

        // Check if another plugin is cancelling the event
        if (event.useInteractedBlock() == Event.Result.DENY) {
            ChatUtils.sendMsg(player, SellstickConfig.territoryMessage, true);
            return;
        }

        event.setCancelled(true); // Cancel opening the chest - confirmed player is using a sellstick

        // Check sellstick material
        if (!ItemUtils.matchSellStickMaterial(sellStick)) {
            // Replace the item if the material does not match
            player.getInventory().removeItem(sellStick);
            CommandUtils.giveSellStick(player, ItemUtils.getUses(sellStick));
            return;
        }
        // Checks if Player has the permission to use a SellStick
        if (!player.hasPermission("sellstick.use")) {
            ChatUtils.sendMsg(player, SellstickConfig.noPerm, true);
            return;
        }
        // Check if player is only holding 1 stick
        if (sellStick.getAmount() != 1) {
            ChatUtils.sendMsg(player, SellstickConfig.holdOneMessage, true);
            return;
        }

        // Handle initial interaction
        if (!EventUtils.handleSellStickInteraction(event)) {
            return;
        }

        // Get total value of container
        double total = EventUtils.calculateContainerWorth(event);

        // Nothing worth selling
        if (total <= 0) {
            if (sendInChat) {
                ChatUtils.sendMsg(player, SellstickConfig.nothingWorth, true);
            } else {
                ChatUtils.sendActionBar(player, SellstickConfig.nothingWorth);
            }
            event.setCancelled(true);
            return;
        }

        // Sell the items
        if (!EventUtils.saleEvent(player, sellStick, total)) {
            if (sendInChat) {
                ChatUtils.sendMsg(player, SellstickConfig.nothingWorth, true);
            } else {
                ChatUtils.sendActionBar(player, SellstickConfig.nothingWorth);
            }
            event.setCancelled(true);
            return;
        }

        // Play sound
        if (SellstickConfig.sound) {
            assert event.getInteractionPoint() != null;
            player.playSound(event.getInteractionPoint(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 0.5f);
        }
    }
}
