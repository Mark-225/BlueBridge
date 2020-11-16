package de.mark225.bluebridge.worldguard.addon;

import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import de.mark225.bluebridge.worldguard.BlueBridgeWG;
import de.mark225.bluebridge.worldguard.config.BlueBridgeWGConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class BlueBridgeWGAddon implements BlueBridgeAddon {
    @Override
    public String name() {
        return "BlueBridgeWG";
    }

    @Override
    public String markerSetName() {
        return BlueBridgeWGConfig.markerSetName();
    }

    @Override
    public boolean defaultHide() {
        return BlueBridgeWGConfig.defaultHideSets();
    }

    @Override
    public Collection<RegionSnapshot> fetchSnapshots(UUID world) {
        WorldGuardIntegration integration = BlueBridgeWG.getInstance().getWGIntegration();
        if(integration != null){
            return integration.getAllRegions(world);
        }
        return Collections.emptyList();
    }

    @Override
    public void reload() {
        BlueBridgeWG.getInstance().updateConfig();
    }
}
