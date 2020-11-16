package de.mark225.bluebridge.core.addon;

import de.mark225.bluebridge.core.region.RegionSnapshot;

import java.util.Collection;
import java.util.UUID;

public interface BlueBridgeAddon {
    public String name();
    public String markerSetName();
    public boolean defaultHide();
    public Collection<RegionSnapshot> fetchSnapshots(UUID world);
    public void reload();
}
