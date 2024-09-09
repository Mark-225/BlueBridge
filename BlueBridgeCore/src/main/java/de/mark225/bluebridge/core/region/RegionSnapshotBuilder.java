package de.mark225.bluebridge.core.region;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.math.Color;
import de.mark225.bluebridge.core.addon.AddonConfig;
import de.mark225.bluebridge.core.addon.BlueBridgeAddon;

import java.util.List;
import java.util.UUID;

public class RegionSnapshotBuilder {

    private RegionSnapshot region;
    private BlueBridgeAddon addon;

    public RegionSnapshotBuilder(BlueBridgeAddon addon, String id, List<Vector2d> points, UUID world) {
        this.addon = addon;
        region = new RegionSnapshot(addon.name(), id, points, world);
        prefillDefaults();
    }

    private void prefillDefaults() {
        AddonConfig cfg = addon.addonConfig();
        region.setHtmlDisplay("unnamed");
        region.setShortName("unnamed");
        region.setHeight(cfg.renderHeight());
        //Probably not all addons support extrusion so it's better to default to false instead of letting the user accidentally set this to true for unsupported addons
        region.setExtrude(false);
        region.setUpperHeight(0f);
        region.setDepthCheck(cfg.defaultDepthCheck());
        region.setColor(cfg.defaultColor());
        region.setBorderColor(cfg.defaultOutlineColor());
        region.setMinDistance(cfg.minDistance());
        region.setMaxDistance(cfg.maxDistance());

    }

    public RegionSnapshotBuilder setHtmlDisplay(String display) {
        region.setHtmlDisplay(display);
        return this;
    }

    public RegionSnapshotBuilder setShortName(String name) {
        region.setShortName(name);
        return this;
    }

    public RegionSnapshotBuilder setHeight(float height) {
        region.setHeight(height);
        return this;
    }

    public RegionSnapshotBuilder setExtrude(boolean extrude) {
        region.setExtrude(extrude);
        return this;
    }

    public RegionSnapshotBuilder setUpperHeight(float upperHeight) {
        region.setUpperHeight(upperHeight);
        return this;
    }

    public RegionSnapshotBuilder setDepthCheck(boolean depthCheck) {
        region.setDepthCheck(depthCheck);
        return this;
    }

    public RegionSnapshotBuilder setColor(Color color) {
        region.setColor(color);
        return this;
    }

    public RegionSnapshotBuilder setBorderColor(Color color) {
        region.setBorderColor(color);
        return this;
    }

    public RegionSnapshotBuilder setMinDistance(double minDistance) {
        region.setMinDistance(minDistance);
        return this;
    }

    public RegionSnapshotBuilder setMaxDistance(double maxDistance) {
        region.setMaxDistance(maxDistance);
        return this;
    }

    public RegionSnapshot build() {
        return region;
    }


}
