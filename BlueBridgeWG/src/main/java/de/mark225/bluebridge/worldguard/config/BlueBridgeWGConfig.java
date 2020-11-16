package de.mark225.bluebridge.worldguard.config;

import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.regex.Pattern;

public class BlueBridgeWGConfig {

    private static FileConfiguration config;
    private static Pattern rgbaRegex = Pattern.compile("[0-9a-f]{8}");
    private static Pattern rgbRegex = Pattern.compile("[0-9a-f]{6}");

    public static void setConfig(FileConfiguration config){
        BlueBridgeWGConfig.config = config;
    }

    public static boolean defaultRender(){
        return config.getBoolean("defaultRender", BlueBridgeConfig.defaultRender());
    }

    public static boolean defaultDepthCheck(){
        return config.getBoolean("defaultDepthCheck", BlueBridgeConfig.defaultDepthCheck());
    }

    public static int renderHeight(){
        return config.getInt("renderHeight", BlueBridgeConfig.renderHeight());
    }

    public static Color defaultColor(){
        String rgba = config.getString("defaultColor", "");
        if(!rgbaRegex.matcher(rgba).matches())
            return BlueBridgeConfig.defaultColor();
        return BlueBridgeUtils.stringToColor(rgba);
    }

    public static Color defaultOutlineColor(){
        String rgb = config.getString("defaultOutlineColor", "");
        if(!rgbRegex.matcher(rgb).matches())
            return BlueBridgeConfig.defaultOutlineColor();
        return BlueBridgeUtils.stringToColor(rgb);
    }

    public static String markerSetName(){
        return config.getString("markerSetName", "Worldguard Regions");
    }

    public static boolean defaultHideSets(){
        return config.getBoolean("hideMarkersets", BlueBridgeConfig.defaultHideSets());
    }

    public static String htmlPreset(){
        return config.getString("htmlPreset", "$(name)");
    }


}
