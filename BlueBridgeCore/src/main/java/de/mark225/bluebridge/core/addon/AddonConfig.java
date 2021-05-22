package de.mark225.bluebridge.core.addon;

import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.regex.Pattern;

public abstract class AddonConfig {

    protected FileConfiguration config;
    private static Pattern rgbaRegex = Pattern.compile("[0-9a-f]{8}");
    private static Pattern rgbRegex = Pattern.compile("[0-9a-f]{6}");

    public void init(FileConfiguration config){
        this.config = config;
    }

    public boolean defaultRender(){
        return config.getBoolean("defaultRender", BlueBridgeConfig.defaultRender());
    }

    public boolean defaultDepthCheck(){
        return config.getBoolean("defaultDepthCheck", BlueBridgeConfig.defaultDepthCheck());
    }

    public int renderHeight(){
        return config.getInt("renderHeight", BlueBridgeConfig.renderHeight());
    }

    public double minDistance() {
        return config.getDouble("minDistance", BlueBridgeConfig.minDistance());
    }

    public double maxDistance() {
        return config.getDouble("maxDistance", BlueBridgeConfig.maxDistance());
    }

    public Color defaultColor(){
        String rgba = config.getString("defaultColor", "");
        if(!rgbaRegex.matcher(rgba).matches())
            return BlueBridgeConfig.defaultColor();
        return BlueBridgeUtils.stringToColor(rgba);
    }

    public Color defaultOutlineColor(){
        String rgb = config.getString("defaultOutlineColor", "");
        if(!rgbRegex.matcher(rgb).matches())
            return BlueBridgeConfig.defaultOutlineColor();
        return BlueBridgeUtils.stringToColor(rgb);
    }

    public String markerSetName(){
        return config.getString("markerSetName", "Undefined (Check BlueBridge addon config)");
    }

    public boolean defaultHideSets(){
        return config.getBoolean("hideMarkersets", BlueBridgeConfig.defaultHideSets());
    }



}
