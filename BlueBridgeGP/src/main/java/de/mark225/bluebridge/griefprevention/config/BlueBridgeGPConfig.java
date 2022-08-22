package de.mark225.bluebridge.griefprevention.config;

import de.bluecolored.bluemap.api.math.Color;
import de.mark225.bluebridge.core.addon.AddonConfig;
import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class BlueBridgeGPConfig extends AddonConfig {
    private static BlueBridgeGPConfig instance;
    private static Pattern rgbaRegex = Pattern.compile("[0-9a-f]{8}");
    private static Pattern rgbRegex = Pattern.compile("[0-9a-f]{6}");

    public static BlueBridgeGPConfig getInstance() {
        return instance;
    }

    public BlueBridgeGPConfig(FileConfiguration config) {
        super();
        instance = this;
        init(config);
    }

    public synchronized boolean defaultExtrude() {
        return config.getBoolean("defaultExtrude", false);
    }

    public synchronized Color adminFillColor() {
        String rgba = config.getString("adminFillColor", "96fd6600");
        if (!rgbaRegex.matcher(rgba).matches())
            rgba = "96fd6600";
        return BlueBridgeUtils.stringToColor(rgba);
    }

    public synchronized Color adminOutlineColor() {
        String rgb = config.getString("adminOutlineColor", "fd6600");
        if (!rgbRegex.matcher(rgb).matches())
            rgb = "fd6600";
        return BlueBridgeUtils.stringToColor(rgb);
    }

    public synchronized String adminDisplayName() {
        return config.getString("adminDisplayName", "Server Claim");
    }

    public synchronized boolean layerChildren() {
        return config.getBoolean("layerChildren", true);
    }
}
