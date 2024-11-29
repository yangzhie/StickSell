package com.shmkane.sellstick.configs;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

// Handles Prices.YML
public class PriceConfig extends Config {

    public PriceConfig(String configName, File dataFolder) {
        super(configName, dataFolder);
    }

    @Override
    void loadValues(FileConfiguration config) {
        config.set("prices.SULPHUR", 1.02);
        config.set("prices.RED_ROSE", 0.76);
        config.set("prices.LEATHER", 2.13);
        config.set("prices.COOKED_BEEF", 0.01);
        config.set("prices.BONE", 5.00);
    }
}
