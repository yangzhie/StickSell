package com.shmkane.sellstick.configs;

import com.shmkane.sellstick.SellStick;
import com.shmkane.sellstick.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

// Handles config.YML
public class SellstickConfig extends Config {

    public static List<String> lore;
    public static String displayName, PriceInterface, receiveMessage, giveMessage, nonSellingRelated, brokenStick,
            nothingWorth, territoryMessage, noPerm, sellMessage, prefix, infiniteLore, finiteLore, holdOneMessage;
    public static boolean sound, glow;
    public static Material material;
    public static int maxAmount;
    static PriceSource priceSource;

    public SellstickConfig(String configName, File dataFolder) {
        super(configName, dataFolder);
    }

    // Load configuration values
    @Override
    void loadValues(FileConfiguration config) {
        // Price Interface Configuration
        PriceInterface = tryGetString(conf, "PriceSource", "PricesYML");

        // Item Configuration
        displayName = tryGetString(conf, "Item.DisplayName", "<gold>SellStick");
        material = tryGetMaterial(conf, "Item.Material", Material.STICK);
        lore = config.getStringList("Item.StickLore");
        finiteLore = tryGetString(conf, "Item.FiniteLore", "<dark_red>%remaining% <red>remaining uses");
        infiniteLore = tryGetString(conf, "Item.InfiniteLore", "<dark_red>Infinite <red>uses!");
        glow = Boolean.parseBoolean(tryGetString(conf, "Item.Glow", String.valueOf(true)));
        maxAmount = Integer.parseInt(tryGetString(conf, "Item.MaxAmount", "1000"));
        sound = Boolean.parseBoolean(tryGetString(conf, "Item.UseSound", String.valueOf(true)));
        
        // Messages
        holdOneMessage = tryGetString(conf, "Messages.OnlyHoldOne", "<red>Please use 1 sell stick at a time!");
        prefix = tryGetString(conf, "Messages.PluginPrefix", "<gold>[<yellow>SellStick<gold>] ");
        sellMessage = tryGetString(conf, "Messages.SellMessage",
                "<red>You sold items for %price% and now have %balance%");
        noPerm = tryGetString(conf, "Messages.NoPermissionMessage", "<red>Sorry, you don''t have permission for this!");
        territoryMessage = tryGetString(conf, "Messages.InvalidTerritoryMessage",
                "<red>You can''t use sell stick here!");
        nothingWorth = tryGetString(conf, "Messages.NotWorthMessage", "<red>Nothing worth selling inside");
        brokenStick = tryGetString(conf, "Messages.BrokenStick", "<red>Your sellstick ran out of uses and broke!");
        nonSellingRelated = tryGetString(conf, "Messages.NonSellingRelated",
                "<red>Oak''s words echoed... There''s a time and place for everything but not now! (Right click a chest!)");
        receiveMessage = tryGetString(conf, "Messages.ReceiveMessage", "<green>You gave %player% %amount% SellSticks!");
        giveMessage = tryGetString(conf, "Messages.GiveMessage", "<green>You''ve received %amount% SellSticks!");

        priceSource = setPriceSource(PriceInterface);
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public static PriceSource getPriceSource() {
        return priceSource;
    }

    private PriceSource setPriceSource(String priceString) {
        if (priceString != null) {
            if (priceString.equalsIgnoreCase("ShopGUI") && SellStick.getInstance().ShopGUIEnabled) {
                return PriceSource.SHOPGUI;

            }
            if (priceString.equalsIgnoreCase("Essentials") && SellStick.getInstance().EssentialsEnabled) {
                return PriceSource.ESSWORTH;
            }
            if (priceString.equalsIgnoreCase("PricesYML")) {
                return PriceSource.PRICESYML;
            }
        } else {
            ChatUtils.log(Level.WARNING, "PriceSource did not match any option. Defaulting to prices.yml.");
            return PriceSource.PRICESYML;
        }
        return PriceSource.PRICESYML;
    }

    public enum PriceSource {
        PRICESYML,
        ESSWORTH,
        SHOPGUI
    }

}
