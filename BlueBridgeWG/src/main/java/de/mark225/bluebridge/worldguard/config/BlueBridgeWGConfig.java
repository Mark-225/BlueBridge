package de.mark225.bluebridge.worldguard.config;

import de.mark225.bluebridge.core.addon.AddonConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class BlueBridgeWGConfig extends AddonConfig {

    private static BlueBridgeWGConfig instance;

    public static BlueBridgeWGConfig getInstance() {
        return instance;
    }

    public BlueBridgeWGConfig(FileConfiguration config) {
        super();
        instance = this;
        init(config);
    }

    public synchronized String htmlPreset() {
        return config.getString("htmlPreset", "$(name)");
    }

    public synchronized boolean defaultExtrude() {
        return config.getBoolean("defaultExtrude", false);
    }


}
