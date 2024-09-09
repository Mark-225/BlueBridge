package de.mark225.bluebridge.core.addon;

import de.mark225.bluebridge.core.region.RegionSnapshot;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public abstract class BlueBridgeAddon {
    public abstract String name();

    public abstract AddonConfig addonConfig();

    public abstract String markerSetName();

    public abstract boolean defaultHide();

    public abstract ConcurrentMap<String, RegionSnapshot> fetchSnapshots(UUID world);

    public boolean supportsAsync(){
        return true;
    }

    public abstract void reload();

    public abstract boolean isActiveAddon();
}
