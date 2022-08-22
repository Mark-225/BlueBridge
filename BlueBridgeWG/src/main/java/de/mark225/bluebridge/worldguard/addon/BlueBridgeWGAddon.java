package de.mark225.bluebridge.worldguard.addon;

import de.mark225.bluebridge.core.addon.AddonConfig;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import de.mark225.bluebridge.worldguard.BlueBridgeWG;
import de.mark225.bluebridge.worldguard.config.BlueBridgeWGConfig;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class BlueBridgeWGAddon extends BlueBridgeAddon {
    @Override
    public String name(){
        return "BlueBridgeWG";
    }

    @Override
    public AddonConfig addonConfig(){
        return BlueBridgeWGConfig.getInstance();
    }

    @Override
    public String markerSetName(){
        return BlueBridgeWGConfig.getInstance().markerSetName();
    }

    @Override
    public boolean defaultHide(){
        return BlueBridgeWGConfig.getInstance().defaultHideSets();
    }

    @Override
    public ConcurrentMap<String, RegionSnapshot> fetchSnapshots(UUID world){
        WorldGuardIntegration integration = BlueBridgeWG.getInstance().getWGIntegration();
        if(integration != null){
            return integration.getAllRegions(world).stream().collect(Collectors.toConcurrentMap(RegionSnapshot::getId, rs -> rs));
        }
        return new ConcurrentHashMap<>();
    }

    @Override
    public void reload(){
        BlueBridgeWG.getInstance().updateConfig();
    }

    @Override
    public boolean isActiveAddon(){
        return false;
    }
}
