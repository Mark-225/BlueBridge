package de.mark225.bluebridge.core;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.bluemap.BlueMapIntegration;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.update.UpdateTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BlueBridgeCore extends JavaPlugin {

    private static BlueBridgeCore instance;

    private BlueMapIntegration blueMapIntegration;

    public static BlueBridgeCore getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        AddonRegistry.clear();
        updateConfig();
    }

    @Override
    public void onEnable() {
        blueMapIntegration = new BlueMapIntegration();
        BlueMapAPI.onEnable(blueMapIntegration::onEnable);
        BlueMapAPI.onDisable(blueMapIntegration::onDisable);
    }


    public void updateConfig() {
        saveDefaultConfig();
        reloadConfig();
        BlueBridgeConfig.setConfig(getConfig());
    }

    public void reloadAddons() {
        AddonRegistry.getAddons().forEach(BlueBridgeAddon::reload);
    }

    public synchronized void startUpdateTask() {
        UpdateTask.setLocked(false);
        UpdateTask.createAndSchedule(true);
    }

    public void addAllActiveRegions() {
        for (BlueBridgeAddon addon : AddonRegistry.getIfActive(true)) {
            for (UUID world : UpdateTask.worlds.keySet()) {
                blueMapIntegration.addOrUpdate(addon.fetchSnapshots(world).values());
            }
        }
    }

    public synchronized void reschedule() {
        UpdateTask.createAndSchedule(false);
    }

    public synchronized void stopUpdateTask() {
        UpdateTask.setLocked(true);
    }

    public BlueMapIntegration getBlueMapIntegration() {
        return blueMapIntegration;
    }


}
