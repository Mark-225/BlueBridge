package de.mark225.bluebridge.core.bluemap;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapAPIListener;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.marker.*;
import de.mark225.bluebridge.core.BlueBridgeCore;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.*;

public class BlueMapIntegration implements BlueMapAPIListener {

    private BlueMapAPI blueMapAPI = null;

    @Override
    public void onEnable(BlueMapAPI blueMapAPI) {
        this.blueMapAPI = blueMapAPI;
        Bukkit.getScheduler().runTask(BlueBridgeCore.getInstance(), () ->{
            BlueBridgeCore.getInstance().updateConfig();
            BlueBridgeCore.getInstance().reloadAddons();
            resetMarkers();
            BlueBridgeCore.getInstance().startUpdateTask();
        });
    }

    @Override
    public void onDisable(BlueMapAPI blueMapApi) {
        BlueBridgeCore.getInstance().stopUpdateTask();
    }

    private void resetMarkers(){
        MarkerAPI markers = loadMarkerAPI();
        if(markers == null)
            return;
        for(MarkerSet set : new ArrayList<>(markers.getMarkerSets())) {
            if (set.getId().startsWith("!BlueBridge_RegionSet#")) {
                markers.removeMarkerSet(set);
            }
        }
        for(BlueBridgeAddon addon : AddonRegistry.getAddons()){
            MarkerSet set = markers.createMarkerSet(getMarkerSetId(addon.name()));
            set.setLabel(addon.markerSetName());
            set.setDefaultHidden(addon.defaultHide());
        }
        try {
            markers.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMarkerSetId(String addon){
        return "!BlueBridge_RegionSet#" + addon;
    }

    public void addOrUpdate(Collection<RegionSnapshot> regions, MarkerAPI markers){
        for(RegionSnapshot rs : regions){
            Shape shape = new Shape(rs.getPoints().toArray(new Vector2d[0]));
            Vector2d midPoint2d = findMidpoint(rs.getPoints());
            Vector3d pos = new Vector3d(midPoint2d.getX(), rs.getHeight(), midPoint2d.getY());
            for(BlueMapMap map : getMapsForWorld(rs.getWorld())) {
                markers.getMarkerSet(getMarkerSetId(rs.getAddon())).ifPresent(ms -> addToMarkerSet(ms, map, rs, shape, pos));
            }
        }
    }

    public void remove(Collection<RegionSnapshot> regions, MarkerAPI markers){
        for(RegionSnapshot rs : regions){
            for(MarkerSet ms : markers.getMarkerSets()){
                for(BlueMapMap map : getMapsForWorld(rs.getWorld())){
                    ms.removeMarker(getGlobalRegionId(rs.getAddon(), rs.getId(), map.getId(), rs.getWorld().toString()));
                }
            }
        }
    }

    public MarkerAPI loadMarkerAPI(){
        try {
            return blueMapAPI.getMarkerAPI();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<BlueMapMap> getMapsForWorld(UUID world){
        ArrayList<BlueMapMap> maps = new ArrayList<>();
        blueMapAPI.getWorld(world).ifPresent(blueMapWorld -> maps.addAll(blueMapWorld.getMaps()));
        return maps;
    }

    private void addToMarkerSet(MarkerSet ms, BlueMapMap map, RegionSnapshot rs, Shape shape, Vector3d pos){
        if(rs.isExtrude()){
            ExtrudeMarker em = ms.createExtrudeMarker(getGlobalRegionId(rs.getAddon(), rs.getId(), map.getId(), rs.getWorld().toString()), map, pos, shape, rs.getHeight(), rs.getUpperHeight());
            em.setLabel(rs.getName());
            em.setColors(rs.getBorderColor(), rs.getColor());
            em.setDepthTestEnabled(rs.getDepthCheck());
            defineDistances(em, rs);
        }else{
            ShapeMarker sm = ms.createShapeMarker(getGlobalRegionId(rs.getAddon(), rs.getId(), map.getId(), rs.getWorld().toString()), map, pos, shape, rs.getHeight());
            sm.setLabel(rs.getName());
            sm.setColors(rs.getBorderColor(), rs.getColor());
            sm.setDepthTestEnabled(rs.getDepthCheck());
            defineDistances(sm, rs);
        }

    }

    private void defineDistances(DistanceRangedMarker dmr, RegionSnapshot rs) {
        final double minDistance = rs.getMinDistance();
        final double maxDistance = rs.getMaxDistance();

        if (minDistance >= 0) {
            dmr.setMinDistance(minDistance);
        }
        if (maxDistance > 0 && maxDistance > minDistance) {
            dmr.setMaxDistance(maxDistance);
        }
    }

    private String getGlobalRegionId(String addon, String region, String mapId, String worldId){
        return "!BlueBridge_RegionMarker#" + addon + "_" + worldId + "/" + mapId + ":" + region;
    }

    private Vector2d findMidpoint(List<Vector2d> polygon){
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        if(polygon.size() >= 0){
            Vector2d first = polygon.get(0);
            minX = first.getX();
            maxX = first.getX();
            minY = first.getY();
            maxY = first.getY();
            for(Vector2d vector : polygon){
                if(vector.getX() < minX)
                    minX = vector.getX();
                if(vector.getX() > maxX)
                    maxX = vector.getX();
                if(vector.getY() < minY)
                    minY = vector.getY();
                if(vector.getY() > maxY)
                    maxY = vector.getY();
            }
        }
        return new Vector2d(minX + ((maxX - minX)/2d), minY + ((maxY - minY)/2d));
    }



}
