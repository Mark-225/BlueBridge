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

import java.util.*;
import java.util.stream.Collectors;

public class BlueMapIntegration {

    private BlueMapAPI blueMapAPI = null;

    public void onEnable(BlueMapAPI blueMapAPI) {
        this.blueMapAPI = blueMapAPI;
        Bukkit.getScheduler().runTask(BlueBridgeCore.getInstance(), () -> {
            BlueBridgeCore.getInstance().updateConfig();
            BlueBridgeCore.getInstance().reloadAddons();
            resetMarkers();
            UpdateTask.worlds.clear();
            UpdateTask.resetLastSnapshots();
            UpdateTask.worlds.addAll(blueMapAPI.getWorlds().stream().map(blueMapWorld -> UUID.fromString(blueMapWorld.getId())).collect(Collectors.toList()));
            BlueBridgeCore.getInstance().addAllActiveRegions();
            BlueBridgeCore.getInstance().startUpdateTask();
        });
    }

    public void onDisable(BlueMapAPI blueMapApi) {
        BlueBridgeCore.getInstance().stopUpdateTask();
    }

    private void resetMarkers() {
        for (BlueBridgeAddon addon : AddonRegistry.getAddons()) {
            for (BlueMapWorld world : blueMapAPI.getWorlds()) {
                for (BlueMapMap map : world.getMaps()) {
                    map.getMarkerSets().put(getMarkerSetId(addon.name()), MarkerSet.builder()
                            .label(addon.markerSetName())
                            .defaultHidden(addon.defaultHide())
                            .build());
                }
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
            for (BlueMapMap map : getMapsForWorld(rs.getWorld())) {
                MarkerSet ms = map.getMarkerSets().get(getMarkerSetId(rs.getAddon()));
                if (ms != null) {
                    addToMarkerSet(ms, map, rs, shape, pos);
                }
            }
        }
    }

    public void remove(Collection<RegionSnapshot> regions) {
        for (RegionSnapshot rs : regions) {
            for (BlueMapMap map : getMapsForWorld(rs.getWorld())) {
                for (Map.Entry<String, MarkerSet> entry : map.getMarkerSets().entrySet()) {
                    entry.getValue().getMarkers().remove(getGlobalRegionId(rs.getAddon(), rs.getId(), map.getId(), rs.getWorld().toString()));
                }
            }
        }
    }

    private List<BlueMapMap> getMapsForWorld(UUID world) {
        ArrayList<BlueMapMap> maps = new ArrayList<>();
        blueMapAPI.getWorld(world).ifPresent(blueMapWorld -> maps.addAll(blueMapWorld.getMaps()));
        return maps;
    }

    private void addToMarkerSet(MarkerSet ms, BlueMapMap map, RegionSnapshot rs, Shape shape, Vector3d pos) {
        if (rs.isExtrude()) {
            ExtrudeMarker em = ExtrudeMarker.builder()
                    .position(pos)
                    .shape(shape, rs.getHeight(), rs.getUpperHeight())
                    .label(StringEscapeUtils.escapeHtml(rs.getShortName()))
                    .detail(rs.getHtmlDisplay())
                    .lineColor(rs.getBorderColor())
                    .fillColor(rs.getColor())
                    .depthTestEnabled(true)
                    .build();
            defineDistances(em, rs);
            ms.getMarkers().put(getGlobalRegionId(rs.getAddon(), rs.getId(), map.getId(), rs.getWorld().toString()), em);
        } else {
            ShapeMarker sm = ShapeMarker.builder()
                    .position(pos)
                    .shape(shape, rs.getHeight())
                    .label(StringEscapeUtils.escapeHtml(rs.getShortName()))
                    .detail(rs.getHtmlDisplay())
                    .lineColor(rs.getBorderColor())
                    .fillColor(rs.getColor())
                    .depthTestEnabled(true)
                    .build();
            defineDistances(sm, rs);
            ms.getMarkers().put(getGlobalRegionId(rs.getAddon(), rs.getId(), map.getId(), rs.getWorld().toString()), sm);
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

    private String getGlobalRegionId(String addon, String region, String mapId, String worldId) {
        return "!BlueBridge_RegionMarker#" + addon + "_" + worldId + "/" + mapId + ":" + region;
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
