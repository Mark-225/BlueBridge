package de.mark225.bluebridge.core.config;

import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.regex.Pattern;

public class BlueBridgeConfig {
    private static FileConfiguration config;
    private static Pattern rgbaRegex = Pattern.compile("[0-9a-f]{8}");
    private static Pattern rgbRegex = Pattern.compile("[0-9a-f]{6}");

    public static void setConfig(FileConfiguration config){
        BlueBridgeConfig.config = config;
    }

    public static int updateInterval(){
        return config.getInt("updateInterval", 200);
    }

    public static boolean defaultRender(){
        return config.getBoolean("defaultRender", true);
    }

    public static boolean defaultHideSets(){
        return config.getBoolean("hideMarkerset", false);
    }

    public static boolean defaultDepthCheck(){
        return config.getBoolean("defaultDepthCheck", false);
    }

    public static int renderHeight(){
        return config.getInt("renderHeight", 63);
    }

    public static Color defaultColor(){
        String rgba = config.getString("defaultColor", "960087ff");
        if(!rgbaRegex.matcher(rgba).matches())
            rgba = "960087ff";
        return BlueBridgeUtils.stringToColor(rgba);
    }

    public static Color defaultOutlineColor(){
        String rgb = config.getString("defaultOutlineColor", "0060ff");
        if(!rgbRegex.matcher(rgb).matches())
            rgb = "0060ff";
        return BlueBridgeUtils.stringToColor(rgb);
    }

    public static boolean debug(){
        return config.getBoolean("debug", false);
    }
}
