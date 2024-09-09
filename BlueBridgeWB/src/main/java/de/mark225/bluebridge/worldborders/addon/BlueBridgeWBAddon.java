package de.mark225.bluebridge.worldborders.addon;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.math.Color;
import de.mark225.bluebridge.core.addon.AddonConfig;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import de.mark225.bluebridge.core.region.RegionSnapshotBuilder;
import de.mark225.bluebridge.worldborders.BlueBridgeWB;
import de.mark225.bluebridge.worldborders.config.BlueBridgeWBConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlueBridgeWBAddon extends BlueBridgeAddon {

    @Override
    public String name() {
        return "BlueBridgeWB";
    }

    @Override
    public AddonConfig addonConfig() {
        return BlueBridgeWBConfig.getInstance();
    }

    @Override
    public String markerSetName() {
        return addonConfig().markerSetName();
    }

    @Override
    public boolean defaultHide() {
        return false;
    }

    @Override
    public ConcurrentMap<String, RegionSnapshot> fetchSnapshots(UUID world) {
        ConcurrentMap<String, RegionSnapshot> map = new ConcurrentHashMap<>();
        World bukkitWorld = Bukkit.getWorld(world);
        if(bukkitWorld == null) return map;
        WorldBorder wb = bukkitWorld.getWorldBorder();
        Vector2d center = new Vector2d(wb.getCenter().getX(), wb.getCenter().getZ());
        double radius = wb.getSize()/2;
        RegionSnapshot region = new RegionSnapshotBuilder(this, "wb_" + bukkitWorld.getName(), List.of(center.add(radius, radius), center.add(-radius, radius), center.add(-radius, -radius), center.add(radius, -radius)), bukkitWorld.getUID())
                .setShortName(BlueBridgeWBConfig.getInstance().markerLabel())
                .setBorderColor(addonConfig().defaultOutlineColor())
                .setColor(new Color(0, 0, 0, 0))
                .setShortName(BlueBridgeWBConfig.getInstance().markerLabel())
                .setHtmlDisplay(BlueBridgeWBConfig.getInstance().markerLabel())
                .setHeight(addonConfig().renderHeight())
                .build();
        map.put(region.getId(), region);
        return map;
    }

    @Override
    public boolean supportsAsync(){
        return false;
    }

    @Override
    public void reload() {
        BlueBridgeWB.getInstance().updateConfig();
    }

    @Override
    public boolean isActiveAddon() {
        return false;
    }
}
