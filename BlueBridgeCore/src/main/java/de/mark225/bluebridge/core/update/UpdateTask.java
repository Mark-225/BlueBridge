package de.mark225.bluebridge.core.update;

import de.mark225.bluebridge.core.BlueBridgeCore;
import de.mark225.bluebridge.core.addon.ActiveAddonEventHandler;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.bluemap.BlueMapIntegration;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class UpdateTask extends BukkitRunnable {

    public static CopyOnWriteArrayList<UUID> worlds = new CopyOnWriteArrayList<>();

    private static ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> lastSnapshots = new ConcurrentHashMap<>();

    private static UpdateTask currentTask;

    private static boolean locked = true;

    public static synchronized void resetLastSnapshots() {
        lastSnapshots.clear();
    }

    public static synchronized void createAndSchedule(boolean instant) {
        if (currentTask == null && !locked) {
            currentTask = new UpdateTask();
            currentTask.runTaskLater(BlueBridgeCore.getInstance(), instant ? 0L : BlueBridgeConfig.updateInterval());
        }
    }

    public static synchronized void setLocked(boolean locked) {
        UpdateTask.locked = locked;
    }

    private UpdateTask() {

    }

    @Override
    public void run() {
        List<BlueBridgeAddon> addons = AddonRegistry.getIfActive(false);
        ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> newSnapshots = new ConcurrentHashMap<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (BlueBridgeAddon addon : addons) {
                    for (UUID world : worlds) {
                        ConcurrentMap<String, RegionSnapshot> worldSnapshots = addon.fetchSnapshots(world);
                        if (newSnapshots.containsKey(addon.name())) {
                            newSnapshots.get(addon.name()).putAll(worldSnapshots);
                        } else {
                            newSnapshots.put(addon.name(), worldSnapshots);
                        }
                    }
                }
                doUpdate(newSnapshots);
            }
        }.runTaskAsynchronously(BlueBridgeCore.getInstance());
    }

    private void doSyncUpdate(BlueMapIntegration integration) {
        ActiveAddonEventHandler.collectAndReset((addedOrUpdated, deleted) -> {
            integration.addOrUpdate(addedOrUpdated);
            integration.remove(deleted);
        });
    }

    private void doUpdate(ConcurrentMap<String, ConcurrentMap<String, RegionSnapshot>> newSnapshots) {
        List<RegionSnapshot> addedOrUpdated = new ArrayList<>();
        List<RegionSnapshot> removed = new ArrayList<>();
        newSnapshots.forEach((addon, regionMap) -> {
            ConcurrentMap<String, RegionSnapshot> lr = lastSnapshots.get(addon);
            if (lr == null)
                lr = new ConcurrentHashMap<>();

            final ConcurrentMap<String, RegionSnapshot> lastRegionMap = lr;

            List<RegionSnapshot> unchangedOrUpdated = new ArrayList<>();
            lastRegionMap.forEach((oldKey, region) -> {
                if (!regionMap.containsKey(oldKey)) {
                    removed.add(region);
                }
            });
            regionMap.forEach((key, region) -> {
                if (!lastRegionMap.containsKey(key)) {
                    addedOrUpdated.add(region);
                } else {
                    unchangedOrUpdated.add(region);
                }
            });
            for (RegionSnapshot rs : unchangedOrUpdated) {
                if (!lastRegionMap.containsValue(rs))
                    addedOrUpdated.add(rs);
            }
        });
        BlueMapIntegration integration = BlueBridgeCore.getInstance().getBlueMapIntegration();
        integration.addOrUpdate(addedOrUpdated);
        integration.remove(removed);
        doSyncUpdate(integration);
        lastSnapshots = newSnapshots;
        reschedule();
    }

    public synchronized void reschedule() {
        currentTask = null;
        BlueBridgeCore.getInstance().reschedule();
    }

}
