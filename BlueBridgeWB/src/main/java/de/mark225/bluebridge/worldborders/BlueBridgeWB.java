package de.mark225.bluebridge.worldborders;

import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.worldborders.addon.BlueBridgeWBAddon;
import de.mark225.bluebridge.worldborders.config.BlueBridgeWBConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class BlueBridgeWB extends JavaPlugin {

    private static BlueBridgeWB instance;

    private static BlueBridgeWBAddon addon;

    public static BlueBridgeWB getInstance() {
        return instance;
    }

    public BlueBridgeWBAddon getAddon() {
        return addon;
    }

    @Override
    public void onLoad() {
        instance = this;
        addon = new BlueBridgeWBAddon();
        updateConfig();
        AddonRegistry.register(addon);
    }

    public void updateConfig() {
        saveDefaultConfig();
        reloadConfig();
        new BlueBridgeWBConfig(getConfig());
    }

}
