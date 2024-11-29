package com.shmkane.sellstick.configs;

import com.google.errorprone.annotations.ForOverride;
import com.shmkane.sellstick.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.logging.Level;

public abstract class Config {

    // The filename used for saving/loading
    protected String configFilename;

    // Local instance of configuration
    protected static File conf;

    Config(String configName, File dataFolder) {
        configFilename = configName + ".yml";
        setup(dataFolder);
    }

    // Setup Main Configuration
    public void setup(File dir) {
        if (dir.exists() || dir.mkdirs()) {
            conf = new File(dir + File.separator + configFilename);
            if (!conf.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(conf);
                try {
                    config.save(conf);
                    ChatUtils.log(Level.INFO, "Finished loading " + configFilename);
                } catch (Exception e) {
                    ChatUtils.log(Level.SEVERE, e.getMessage());
                }
            }
            loadValues(getConfig());
        }
    }

    @ForOverride
    void loadValues(FileConfiguration config) {
    }

    public static FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(conf);
    }

    void addMissingField(File yamlConfiguration, String sField, String sDefault) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(yamlConfiguration);
        if (!config.contains(sField)) {
            ChatUtils.log(Level.WARNING, "Adding missing " + sField + " to " + yamlConfiguration.getName());
            config.set(sField, sDefault);
            try {
                config.save(yamlConfiguration);
            } catch (Exception e) {
                ChatUtils.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    Material tryGetMaterial(File yamlConfiguration, String sField, Material mDefault) {
        // Missing field
        addMissingField(yamlConfiguration, sField, mDefault.name());
        // Try load value, otherwise use default
        try {
            String field = YamlConfiguration.loadConfiguration(yamlConfiguration).getString(sField);
            if (field != null) {
                Material m = Material.getMaterial(field);
                if (m != null) return m;
            }
            ChatUtils.log(Level.SEVERE, "Could not find material: " + sField + ". Using default: " + mDefault.name());
            return mDefault;
        } catch(Exception e) {
            ChatUtils.log(Level.SEVERE, "Error loading value from" + sField + " in " + yamlConfiguration.getName());
            return mDefault;
        }
    }

    String tryGetString(File yamlConfiguration, String sField, String sDefault) {
        // Missing field
        addMissingField(yamlConfiguration, sField, sDefault);
        // Try load value, otherwise use default
        try {
            return YamlConfiguration.loadConfiguration(yamlConfiguration).getString(sField);
        } catch(NullPointerException e) {
            ChatUtils.log(Level.SEVERE, "Error loading value from" + sField + " in " + yamlConfiguration.getName());
            return sDefault;
        }
    }
}
