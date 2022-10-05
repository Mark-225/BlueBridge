package de.mark225.bluebridge.core.addon;

import de.bluecolored.bluemap.api.math.Color;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AddonConfig {

    protected FileConfiguration config;
    private static Pattern rgbaRegex = Pattern.compile("[0-9a-f]{8}");
    private static Pattern rgbRegex = Pattern.compile("[0-9a-f]{6}");

    public synchronized void init(FileConfiguration config) {
        this.config = config;
    }

    public synchronized boolean defaultRender() {
        return config.getBoolean("defaultRender", BlueBridgeConfig.defaultRender());
    }

    public synchronized boolean defaultDepthCheck() {
        return config.getBoolean("defaultDepthCheck", BlueBridgeConfig.defaultDepthCheck());
    }

    public synchronized int renderHeight() {
        return config.getInt("renderHeight", BlueBridgeConfig.renderHeight());
    }

    public synchronized double minDistance() {
        return config.getDouble("minDistance", BlueBridgeConfig.minDistance());
    }

    public synchronized double maxDistance() {
        return config.getDouble("maxDistance", BlueBridgeConfig.maxDistance());
    }

    public synchronized Color defaultColor() {
        String rgba = config.getString("defaultColor", "");
        if (!rgbaRegex.matcher(rgba).matches())
            return BlueBridgeConfig.defaultColor();
        return new Color("#" + rgba);
    }

    public synchronized Color defaultOutlineColor() {
        String rgb = config.getString("defaultOutlineColor", "");
        if (!rgbRegex.matcher(rgb).matches())
            return BlueBridgeConfig.defaultOutlineColor();
        return new Color("#" + rgb);
    }

    public synchronized String markerSetName() {
        return config.getString("markerSetName", "Undefined (Check BlueBridge addon config)");
    }

    public synchronized boolean defaultHideSets() {
        return config.getBoolean("hideMarkersets", BlueBridgeConfig.defaultHideSets());
    }

    public synchronized List<String> excludedMaps() {
        List<String> worlds = new ArrayList<>();
        worlds.addAll(config.getStringList("excludedMaps"));
        worlds.addAll(BlueBridgeConfig.excludedMaps());
        return worlds;
    }


}
