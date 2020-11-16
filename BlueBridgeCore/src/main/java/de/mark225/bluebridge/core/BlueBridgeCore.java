package de.mark225.bluebridge.core;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.bluemap.BlueMapIntegration;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.update.UpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BlueBridgeCore extends JavaPlugin {

    private static BlueBridgeCore instance;

    private UpdateTask updateTask;
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
        getLogger().log(Level.INFO, "Enabling BlueBridge Core");

        blueMapIntegration = new BlueMapIntegration();
        BlueMapAPI.registerListener(blueMapIntegration);

    }


    public void updateConfig(){
        saveDefaultConfig();
        reloadConfig();
        BlueBridgeConfig.setConfig(getConfig());
    }

    public void reloadAddons(){
        AddonRegistry.getAddons().forEach(addon -> addon.reload());
    }

    public void startUpdateTask(){
        if(updateTask == null){
            updateTask = new UpdateTask();
            updateTask.runTaskTimer(this, 1, BlueBridgeConfig.updateInterval());
        }
    }

    public void stopUpdateTask(){
        if(updateTask != null){
            Bukkit.getScheduler().cancelTask(updateTask.getTaskId());
            updateTask = null;
        }
    }

    public BlueMapIntegration getBlueMapIntegration(){
        return blueMapIntegration;
    }


}
