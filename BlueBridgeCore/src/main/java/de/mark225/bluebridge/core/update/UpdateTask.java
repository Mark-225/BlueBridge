package de.mark225.bluebridge.core.update;

import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.mark225.bluebridge.core.BlueBridgeCore;
import de.mark225.bluebridge.core.addon.ActiveAddonEventHandler;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.bluemap.BlueMapIntegration;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateTask extends BukkitRunnable {

    public static CopyOnWriteArrayList<UUID> worlds = new CopyOnWriteArrayList<>();

    private static ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> lastSnapshots = new ConcurrentHashMap<>();

    private static UpdateTask currentTask;

    private static boolean locked = true;

    public static synchronized void resetLastSnapshots(){
        lastSnapshots.clear();
    }

    public static synchronized void createAndSchedule(boolean instant){
        if(currentTask == null && !locked){
            currentTask = new UpdateTask();
            currentTask.runTaskLater(BlueBridgeCore.getInstance(), instant ? 0L : BlueBridgeConfig.updateInterval());
        }
    }

    public static synchronized void setLocked(boolean locked){
        UpdateTask.locked = locked;
    }

    private UpdateTask(){

    }

    @Override
    public void run() {
        List<BlueBridgeAddon> addons = AddonRegistry.getIfActive(false);
        ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> newSnapshots = new ConcurrentHashMap<>();
        new BukkitRunnable() {
                @Override
                public void run() {
                    for(BlueBridgeAddon addon : addons){
                        for(UUID world : worlds) {
                            ConcurrentMap<String, RegionSnapshot> worldSnapshots = addon.fetchSnapshots(world);
                            if(newSnapshots.containsKey(addon.name())){
                                newSnapshots.get(addon.name()).putAll(worldSnapshots);
                            }else {
                                newSnapshots.put(addon.name(), worldSnapshots);
                            }
                        }
                    }
                    doUpdate(newSnapshots);
                }
        }.runTaskAsynchronously(BlueBridgeCore.getInstance());
    }

    private void doSyncUpdate(BlueMapIntegration integration, MarkerAPI api){
        ActiveAddonEventHandler.collectAndReset((addedOrUpdated, deleted) ->{
            integration.addOrUpdate(addedOrUpdated, api);
            integration.remove(deleted, api);
        });
    }

    private void doUpdate(ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> newSnapshots){
        List<RegionSnapshot> addedOrUpdated = new ArrayList<>();
        List<RegionSnapshot> removed = new ArrayList<>();
        newSnapshots.forEach((addon, regionMap) ->{
            ConcurrentMap<String, RegionSnapshot> lr = lastSnapshots.get(addon);
            if(lr == null)
                lr = new ConcurrentHashMap<>();

            final ConcurrentMap<String, RegionSnapshot> lastRegionMap = lr;

            List<RegionSnapshot> unchangedOrUpdated = new ArrayList<>();
            lastRegionMap.forEach((oldKey, region) ->{
                if(!regionMap.containsKey(oldKey)){
                    removed.add(region);
                }
            });
            regionMap.forEach((key, region) ->{
                if(!lastRegionMap.containsKey(key)){
                    addedOrUpdated.add(region);
                }else{
                    unchangedOrUpdated.add(region);
                }
            });
            for(RegionSnapshot rs : unchangedOrUpdated){
                if(!lastRegionMap.containsValue(rs))
                    addedOrUpdated.add(rs);
            }
        });
        BlueMapIntegration integration = BlueBridgeCore.getInstance().getBlueMapIntegration();
        MarkerAPI api = integration.loadMarkerAPI();
        integration.addOrUpdate(addedOrUpdated, api);
        integration.remove(removed, api);
        doSyncUpdate(integration, api);
        //"Workaround" for very rare exceptions while saving by retrying up to two times
        Logger logger = BlueBridgeCore.getInstance().getLogger();
        int retries = 3;
        while (retries > 0) {
            try {
                api.save();
                break;
            } catch (IOException e) {
                e.printStackTrace();
                retries--;
                if(retries > 0 ){
                    logger.log(Level.INFO, "BlueBridge save attempt failed. Retrying in 0.5 seconds");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }else{
                    logger.log(Level.WARNING, "BlueBridge Marker save failed after 3 attempts. Some markers might be out of sync with their respective regions.");
                }
            }
        }
        lastSnapshots = newSnapshots;
        reschedule();
    }

    public synchronized void reschedule(){
        currentTask = null;
        BlueBridgeCore.getInstance().reschedule();
    }

}
