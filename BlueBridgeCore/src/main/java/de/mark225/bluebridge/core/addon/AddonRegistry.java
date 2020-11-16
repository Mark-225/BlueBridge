package de.mark225.bluebridge.core.addon;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

public class AddonRegistry {
    private static List<BlueBridgeAddon> registeredAddons;

    public static void clear(){
        registeredAddons = new ArrayList<>();
    }

    public static void register(BlueBridgeAddon addon){
        if(getAddon(addon.name()) == null){
            registeredAddons.add(addon);
        }
    }

    public static List<BlueBridgeAddon> getAddons(){
        return ImmutableList.copyOf(registeredAddons);
    }

    public static BlueBridgeAddon getAddon(String id){
        for(BlueBridgeAddon addon : registeredAddons){
            if(addon.name().equals(id))
                return addon;
        }
        return null;
    }



}
