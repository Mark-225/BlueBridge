package de.mark225.bluebridge.core.bluemap;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.BlueMapWorld;
import de.bluecolored.bluemap.api.markers.DistanceRangedMarker;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Shape;
import de.mark225.bluebridge.core.BlueBridgeCore;
import de.mark225.bluebridge.core.addon.AddonRegistry;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import de.mark225.bluebridge.core.update.UpdateTask;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlueMapIntegration {

    private BlueMapAPI blueMapAPI = null;

    private Map<AbstractMap.SimpleEntry<String, UUID>, MarkerSet> markerSets = new ConcurrentHashMap<>();

    public void onEnable(BlueMapAPI blueMapAPI) {
        this.blueMapAPI = blueMapAPI;
        Bukkit.getScheduler().runTask(BlueBridgeCore.getInstance(), () -> {
            BlueBridgeCore.getInstance().updateConfig();
            BlueBridgeCore.getInstance().reloadAddons();
            createMarkerSets();
            UpdateTask.worlds.clear();
            UpdateTask.resetLastSnapshots();
            for (World bukkitWorld : Bukkit.getWorlds()) {
                UUID uuid = bukkitWorld.getUID();
                blueMapAPI.getWorld(uuid).ifPresent(blueMapWorld -> UpdateTask.worlds.put(uuid, blueMapWorld));
            }
            BlueBridgeCore.getInstance().addAllActiveRegions();
            BlueBridgeCore.getInstance().startUpdateTask();
        });
    }

    public void onDisable(BlueMapAPI blueMapApi) {
        BlueBridgeCore.getInstance().stopUpdateTask();
    }

    private void createMarkerSets() {
        markerSets.clear();
        for (World w : Bukkit.getWorlds()) {
            Optional<BlueMapWorld> oWorld = blueMapAPI.getWorld(w.getUID());
            if(oWorld.isEmpty()) continue;
            BlueMapWorld blueMapWorld = oWorld.get();
            for (BlueBridgeAddon addon : AddonRegistry.getAddons()) {
                MarkerSet markerSet = MarkerSet.builder()
                        .label(addon.markerSetName())
                        .defaultHidden(addon.defaultHide())
                        .build();
                markerSets.put(new AbstractMap.SimpleEntry<>(addon.name(), w.getUID()), markerSet);
                String markerSetId = addon.name() + "-" + w.getUID();
                blueMapWorld.getMaps().forEach(blueMapMap -> blueMapMap.getMarkerSets().put(markerSetId, markerSet));
            }
        }
    }

    private String getMarkerSetId(String addon) {
        return "!BlueBridge_RegionSet#" + addon;
    }

    public void addOrUpdate(Collection<RegionSnapshot> regions) {
        for (RegionSnapshot rs : regions) {
            Shape shape = new Shape(rs.getPoints().toArray(new Vector2d[0]));
            Vector2d midPoint2d = findMidpoint(rs.getPoints());
            Vector3d pos = new Vector3d(midPoint2d.getX(), rs.getHeight(), midPoint2d.getY());
            Optional<MarkerSet> ms = getMarkerSet(rs.getAddon(), rs.getWorld());
            ms.ifPresent(markerSet -> addToMarkerSet(markerSet, rs, shape, pos));
        }
    }

    public void remove(Collection<RegionSnapshot> regions) {
        for (RegionSnapshot rs : regions) {
            getMarkerSet(rs.getAddon(), rs.getWorld()).ifPresent(markerSet -> {
                markerSet.getMarkers().remove(getGlobalRegionId(rs.getAddon(), rs.getId(), rs.getWorld().toString()));
            });
        }
    }

    private Collection<BlueMapMap> getMapsForWorld(UUID world) {
        return UpdateTask.worlds.get(world).getMaps();
    }

    private void addToMarkerSet(MarkerSet ms, RegionSnapshot rs, Shape shape, Vector3d pos) {
        if (rs.isExtrude()) {
            ExtrudeMarker em = ExtrudeMarker.builder()
                    .position(pos)
                    .shape(shape, rs.getHeight(), rs.getUpperHeight())
                    .label(StringEscapeUtils.escapeHtml(rs.getShortName()))
                    .detail(rs.getHtmlDisplay())
                    .lineColor(rs.getBorderColor())
                    .fillColor(rs.getColor())
                    .depthTestEnabled(rs.getDepthCheck())
                    .build();
            defineDistances(em, rs);
            ms.getMarkers().put(getGlobalRegionId(rs.getAddon(), rs.getId(), rs.getWorld().toString()), em);
        } else {
            ShapeMarker sm = ShapeMarker.builder()
                    .position(pos)
                    .shape(shape, rs.getHeight())
                    .label(StringEscapeUtils.escapeHtml(rs.getShortName()))
                    .detail(rs.getHtmlDisplay())
                    .lineColor(rs.getBorderColor())
                    .fillColor(rs.getColor())
                    .depthTestEnabled(rs.getDepthCheck())
                    .build();
            defineDistances(sm, rs);
            ms.getMarkers().put(getGlobalRegionId(rs.getAddon(), rs.getId(), rs.getWorld().toString()), sm);
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

    private String getGlobalRegionId(String addon, String region, String worldId) {
        return "!BlueBridge_RegionMarker#" + addon + "_" + worldId + ":" + region;
    }

    private Optional<MarkerSet> getMarkerSet(String addon, UUID world){
        return Optional.ofNullable(markerSets.get(new AbstractMap.SimpleEntry<>(addon, world)));
    }

    private Vector2d findMidpoint(List<Vector2d> polygon) {
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        Vector2d first = polygon.get(0);
        minX = first.getX();
        maxX = first.getX();
        minY = first.getY();
        maxY = first.getY();
        for (Vector2d vector : polygon) {
            if (vector.getX() < minX)
                minX = vector.getX();
            if (vector.getX() > maxX)
                maxX = vector.getX();
            if (vector.getY() < minY)
                minY = vector.getY();
            if (vector.getY() > maxY)
                maxY = vector.getY();
        }
        return new Vector2d(minX + ((maxX - minX) / 2d), minY + ((maxY - minY) / 2d));
    }


}
