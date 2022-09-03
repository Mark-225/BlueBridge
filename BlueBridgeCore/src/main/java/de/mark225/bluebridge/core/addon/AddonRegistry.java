package de.mark225.bluebridge.core.addon;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class AddonRegistry {
    private static CopyOnWriteArrayList<BlueBridgeAddon> registeredAddons = new CopyOnWriteArrayList<>();

    public static void clear() {
        registeredAddons.clear();
    }

    public static void register(BlueBridgeAddon addon) {
        if (getAddon(addon.name()) == null) {
            registeredAddons.add(addon);
        }
    }

    public static List<BlueBridgeAddon> getAddons() {
        return ImmutableList.copyOf(registeredAddons);
    }

    public static BlueBridgeAddon getAddon(String id) {
        for (BlueBridgeAddon addon : registeredAddons) {
            if (addon.name().equals(id))
                return addon;
        }
        return null;
    }

    public static List<BlueBridgeAddon> getIfActive(boolean active) {
        return registeredAddons.stream().filter(addon -> active == addon.isActiveAddon()).collect(Collectors.toList());
    }


}
