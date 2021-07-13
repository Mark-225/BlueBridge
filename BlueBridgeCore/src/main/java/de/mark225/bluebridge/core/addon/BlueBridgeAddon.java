package de.mark225.bluebridge.core.addon;

import de.mark225.bluebridge.core.region.RegionSnapshot;

import javax.swing.plaf.synth.Region;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BlueBridgeAddon {
    public abstract String name();
    public abstract AddonConfig addonConfig();
    public abstract String markerSetName();
    public abstract boolean defaultHide();
    public abstract ConcurrentMap<String, RegionSnapshot> fetchSnapshots(UUID world);
    public abstract void reload();
    public abstract boolean isActiveAddon();
}
