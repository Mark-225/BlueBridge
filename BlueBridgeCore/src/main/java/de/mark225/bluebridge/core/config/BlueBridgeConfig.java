package de.mark225.bluebridge.core.config;

import de.bluecolored.bluemap.api.math.Color;
import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class BlueBridgeConfig {
    private static FileConfiguration config;
    private static Pattern rgbaRegex = Pattern.compile("[0-9a-f]{8}");
    private static Pattern rgbRegex = Pattern.compile("[0-9a-f]{6}");

    public static synchronized void setConfig(FileConfiguration config) {
        BlueBridgeConfig.config = config;
    }

    public static synchronized int updateInterval() {
        return config.getInt("updateInterval", 200);
    }

    public static synchronized boolean defaultRender() {
        return config.getBoolean("defaultRender", true);
    }

    public static synchronized boolean defaultHideSets() {
        return config.getBoolean("hideMarkerset", false);
    }

    public static synchronized boolean defaultDepthCheck() {
        return config.getBoolean("defaultDepthCheck", false);
    }

    public static synchronized int renderHeight() {
        return config.getInt("renderHeight", 63);
    }

    public static synchronized double minDistance() {
        return config.getDouble("minDistance", 10.0);
    }

    public static synchronized double maxDistance() {
        return config.getDouble("maxDistance", 500.0);
    }

    public static synchronized Color defaultColor() {
        String rgba = config.getString("defaultColor", "960087ff");
        if (!rgbaRegex.matcher(rgba).matches())
            rgba = "960087ff";
        return new Color("#" + rgba);
    }

    public static synchronized Color defaultOutlineColor() {
        String rgb = config.getString("defaultOutlineColor", "0060ff");
        if (!rgbRegex.matcher(rgb).matches())
            rgb = "0060ff";
        return new Color("#" + rgb);
    }

    public static synchronized boolean debug() {
        return config.getBoolean("debug", false);
    }
}
