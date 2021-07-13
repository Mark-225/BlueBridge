package de.mark225.bluebridge.core.addon;

import de.mark225.bluebridge.core.region.RegionSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class ActiveAddonEventHandler {
    private static List<RegionSnapshot> addedOrUpdated = new ArrayList<>();
    private static List<RegionSnapshot> deleted = new ArrayList<>();

    public static void addOrUpdate(RegionSnapshot region){
        addedOrUpdated.add(region);
    }

    public static  void delete(RegionSnapshot region){
        deleted.add(region);
    }

    public static void resetLists(){
        addedOrUpdated = new ArrayList<>();
        deleted = new ArrayList<>();
    }

    public static void collectAndReset(BiConsumer<List<RegionSnapshot>, List<RegionSnapshot>> callback){
        callback.accept(addedOrUpdated, deleted);
        resetLists();
    }

}
