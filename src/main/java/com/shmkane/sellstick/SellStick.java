package com.shmkane.sellstick;

import com.shmkane.sellstick.events.PlayerListener;
import com.shmkane.sellstick.utilities.ChatUtils;
import com.shmkane.sellstick.configs.SellstickConfig;
import com.shmkane.sellstick.configs.PriceConfig;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/*
 * SellStick is a Minecraft plugin that allows customizable
 * selling of chest, shulker and barrel contents.
 *
 * @author shmkane, TreemanK, CodfishBender, 1UnderTheSun
 */

public class SellStick extends JavaPlugin {

    private static Economy econ = null;
    public boolean ShopGUIEnabled, EssentialsEnabled, CommandAPIEnabled = false;

    SellstickConfig sellstickConfig;
    PriceConfig priceConfig;
    static SellStick plugin;

    /**
     * Initial plugin setup. Creation and loading of YML files.
     * <p>
     * Creates / Loads Config.yml
     * Creates / Loads Prices.yml
     * Saves Current Config.
     * Create instance of Essentials
     * Hook SellStickCommand executor
     */

    @Override
    public void onEnable() {
        plugin = this;
        // Don't load plugin if Vault is not present
        if (!setupEconomy()) {
            ChatUtils.log(Level.SEVERE, SellstickConfig.prefix + " - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        saveDefaultConfig();

        // Load Variables, Listeners and Commands
        loadVariables();
        loadClasses();
    }

    // Reload plugin (only configurations && variables)
    public void reload() {
        // Update config file
        reloadConfig();
        // Check soft dependencies and update interface
        loadVariables();
        // Update config vars
        sellstickConfig.setup(getDataFolder());
        priceConfig.setup(getDataFolder());
    }

    public void loadVariables() {
        // Check Soft Dependencies
        ShopGUIEnabled = Bukkit.getPluginManager().isPluginEnabled("ShopGuiPlus");
        EssentialsEnabled = Bukkit.getPluginManager().isPluginEnabled("Essentials");
    }

    public void loadClasses() {
        // Register Listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        // Register Commands
        getCommand("sellstick").setExecutor(new SellStickCommand());
        // Create config classes
        sellstickConfig = new SellstickConfig("config", getDataFolder());
        priceConfig = new PriceConfig("prices", getDataFolder());
    }

    @Override
    public void onDisable() {

    }

    // Vault Economy Provider
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public Economy getEcon() {
        return SellStick.econ;
    }

    public static SellStick getInstance() {
        return plugin;
    }

    public int getMaxAmount() {
        return sellstickConfig.getMaxAmount();
    }
}
