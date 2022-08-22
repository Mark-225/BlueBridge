package de.mark225.bluebridge.worldguard;

import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.worldguard.addon.BlueBridgeWGAddon;
import de.mark225.bluebridge.worldguard.addon.WorldGuardIntegration;
import de.mark225.bluebridge.worldguard.config.BlueBridgeWGConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class BlueBridgeWG extends JavaPlugin {

    private static BlueBridgeWG instance;
    private static BlueBridgeWGAddon addon;
    private WorldGuardIntegration integration;

    public static BlueBridgeWG getInstance() {
        return instance;
    }

    public WorldGuardIntegration getWGIntegration() {
        return integration;
    }

    public BlueBridgeWGAddon getAddon() {
        return addon;
    }

    @Override
    public void onLoad() {
        instance = this;
        updateConfig();
        integration = new WorldGuardIntegration();
        integration.init();
        addon = new BlueBridgeWGAddon();
        AddonRegistry.register(addon);
    }

    public void updateConfig() {
        saveDefaultConfig();
        reloadConfig();
        new BlueBridgeWGConfig(getConfig());
    }

}
