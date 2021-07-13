package de.mark225.bluebridge.core;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.bluemap.BlueMapIntegration;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.update.UpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BlueBridgeCore extends JavaPlugin {

    private static BlueBridgeCore instance;

    private BlueMapIntegration blueMapIntegration;

    public static BlueBridgeCore getInstance(){
        return instance;
    }

    @Override
    public void onLoad(){
        instance = this;
        AddonRegistry.clear();
        updateConfig();
    }

    @Override
    public void onEnable(){
        blueMapIntegration = new BlueMapIntegration();
        BlueMapAPI.onEnable(blueMapIntegration::onEnable);
        BlueMapAPI.onDisable(blueMapIntegration::onDisable);
    }


    public void updateConfig(){
        saveDefaultConfig();
        reloadConfig();
        BlueBridgeConfig.setConfig(getConfig());
    }

    public void reloadAddons(){
        AddonRegistry.getAddons().forEach(addon -> addon.reload());
    }

    public synchronized void startUpdateTask(){
        UpdateTask.setLocked(false);
        UpdateTask.createAndSchedule(true);
    }

    public void addAllActiveRegions(){
        MarkerAPI api = blueMapIntegration.loadMarkerAPI();
        for(BlueBridgeAddon addon : AddonRegistry.getIfActive(true)){
            for(UUID world : UpdateTask.worlds){
                blueMapIntegration.addOrUpdate(addon.fetchSnapshots(world).values(), api);
            }
        }
        try {
            api.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void reschedule(){
        UpdateTask.createAndSchedule(false);
    }

    public synchronized void stopUpdateTask(){
        UpdateTask.setLocked(true);
    }

    public BlueMapIntegration getBlueMapIntegration(){
        return blueMapIntegration;
    }


}
