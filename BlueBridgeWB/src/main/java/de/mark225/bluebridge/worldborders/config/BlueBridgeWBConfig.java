package de.mark225.bluebridge.worldborders.config;

import de.mark225.bluebridge.core.addon.AddonConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class BlueBridgeWBConfig extends AddonConfig {

        private static BlueBridgeWBConfig instance;

        public static BlueBridgeWBConfig getInstance() {
            return instance;
        }

        public BlueBridgeWBConfig(FileConfiguration config) {
            super();
            instance = this;
            init(config);
        }

        @Override
        public synchronized String markerSetName() {
            return config.getString("markerSetName", "World borders");
        }

        public synchronized String markerLabel() {
            return config.getString("markerLabel", "World border");
        }
}
