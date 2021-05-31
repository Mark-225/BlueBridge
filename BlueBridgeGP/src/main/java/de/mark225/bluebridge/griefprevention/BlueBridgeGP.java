package de.mark225.bluebridge.griefprevention;

import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.griefprevention.addon.BlueBridgeGPAddon;
import de.mark225.bluebridge.griefprevention.addon.GriefPreventionIntegration;
import de.mark225.bluebridge.griefprevention.config.BlueBridgeGPConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class BlueBridgeGP extends JavaPlugin {
    private static BlueBridgeGP instance;
    private static BlueBridgeGPAddon addon;
    private GriefPreventionIntegration integration;

    public static BlueBridgeGP getInstance(){
        return instance;
    }

    public GriefPreventionIntegration getGPIntegration(){
        return integration;
    }

    public BlueBridgeGPAddon getAddon(){
        return addon;
    }

    @Override
    public void onLoad(){
        instance = this;
        updateConfig();
        integration = new GriefPreventionIntegration();
        addon = new BlueBridgeGPAddon();
        AddonRegistry.register(addon);
    }

    @Override
    public void onEnable(){
        integration.init();
    }

    public void updateConfig(){
        saveDefaultConfig();
        reloadConfig();
        new BlueBridgeGPConfig(getConfig());
    }

}
