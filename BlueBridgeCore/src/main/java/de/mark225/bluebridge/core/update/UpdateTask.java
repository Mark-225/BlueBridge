package de.mark225.bluebridge.core.update;

import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.mark225.bluebridge.core.BlueBridgeCore;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.bluemap.BlueMapIntegration;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class UpdateTask extends BukkitRunnable {

    public static CopyOnWriteArrayList<UUID> worlds = new CopyOnWriteArrayList<>();

    private static ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> lastSnapshots = new ConcurrentHashMap<>();

    private static UpdateTask currentTask;

    private static boolean locked = true;

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
        List<BukkitRunnable> tasks = new CopyOnWriteArrayList<>();
        List<BlueBridgeAddon> addons = AddonRegistry.getIfActive(false);
        ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> newSnapshots = new ConcurrentHashMap<>();
        for(UUID world : worlds){
            tasks.add(new BukkitRunnable() {
                @Override
                public void run() {
                    for(BlueBridgeAddon addon : addons){
                        newSnapshots.put(addon.name(), addon.fetchSnapshots(world));
                    }
                    tasks.remove(this);
                    if(tasks.isEmpty())
                        doUpdate(newSnapshots);
                }
            });
        }
        for(BukkitRunnable task : tasks){
            task.runTaskAsynchronously(BlueBridgeCore.getInstance());
        }
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
        try {
            api.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastSnapshots = newSnapshots;
        reschedule();
    }

    public synchronized void reschedule(){
        currentTask = null;
        BlueBridgeCore.getInstance().reschedule();
    }

}
