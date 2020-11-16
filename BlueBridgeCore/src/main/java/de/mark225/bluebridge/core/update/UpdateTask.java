package de.mark225.bluebridge.core.update;

import de.bluecolored.bluemap.api.marker.MarkerAPI;
import de.mark225.bluebridge.core.BlueBridgeCore;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.bluemap.BlueMapIntegration;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateTask extends BukkitRunnable {

    List<RegionSnapshot> lastSnapshots = new ArrayList<>();

    @Override
    public void run() {

        List<RegionSnapshot> currentUpdate = new ArrayList<>();

        //Schedule one task per World to fetch all regions
        int tickDelay = 0;
        for(World w : Bukkit.getWorlds()){
            new BukkitRunnable(){
                @Override
                public void run() {
                    for(BlueBridgeAddon addon : AddonRegistry.getAddons()){
                        currentUpdate.addAll(addon.fetchSnapshots(w.getUID()));
                    }
                }
            }.runTaskLater(BlueBridgeCore.getInstance(), tickDelay++);
        }

        //Schedule task one tick after fetch tasks are completed
        new BukkitRunnable(){

            @Override
            public void run() {
                List<RegionSnapshot> lastUpdate = lastSnapshots;
                lastSnapshots = new ArrayList<>(currentUpdate);
                async(lastUpdate, currentUpdate);
            }
        }.runTaskLater(BlueBridgeCore.getInstance(), tickDelay);

    }

    private void async(List<RegionSnapshot> last, List<RegionSnapshot> current){
        new BukkitRunnable(){
            @Override
            public void run() {
                List<RegionSnapshot> changedOrAdded = current.stream().filter(rs -> !last.contains(rs)).collect(Collectors.toList());
                List<RegionSnapshot> removed = last.stream().filter(rs -> current.stream().noneMatch(rscur -> rs.refersSameRegion(rscur))).collect(Collectors.toList());
                BlueMapIntegration bmi = BlueBridgeCore.getInstance().getBlueMapIntegration();
                MarkerAPI markers = bmi.loadMarkerAPI();
                bmi.addOrUpdate(changedOrAdded, markers);
                bmi.remove(removed, markers);
                try {
                    markers.save();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(BlueBridgeCore.getInstance());
    }


}
