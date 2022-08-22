package de.mark225.bluebridge.core.region;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.math.Color;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Class that stores all needed Information about a Worldguard region, so it can be accessed asynchronously
 */
public class RegionSnapshot {
    private String addon;
    private String id;
    private String htmlDisplay = "undefined";
    private String shortName = "undefined";
    private UUID world;
    private float height = 0;
    private boolean extrude = false;
    private float upperHeight = 0;
    private boolean depthCheck = false;
    private List<Vector2d> points;
    private Color color = new Color(0, 0, 0);
    private Color borderColor = new Color(0, 0, 0);
    private double minDistance;
    private double maxDistance;

    public RegionSnapshot(String addon, String id, List<Vector2d> points, UUID world) {
        this.addon = addon;
        this.id = id;
        this.points = points;
        this.world = world;
    }

    public String getAddon() {
        return addon;
    }

    public String getId() {
        return id;
    }

    public float getHeight() {
        return height;
    }

    public boolean isExtrude() {
        return extrude;
    }

    public float getUpperHeight() {
        return upperHeight;
    }

    public boolean getDepthCheck() {
        return depthCheck;
    }

    public String getHtmlDisplay() {
        return htmlDisplay;
    }

    public String getShortName() {
        return shortName;
    }

    public UUID getWorld() {
        return world;
    }

    public List<Vector2d> getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setHtmlDisplay(String htmlDisplay) {
        this.htmlDisplay = htmlDisplay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddon(), getId(), getHtmlDisplay(), getShortName(), getWorld(), getHeight(), isExtrude(), getUpperHeight(), getDepthCheck(), getPoints(), getColor(), getBorderColor(), getMinDistance(), getMaxDistance());
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setExtrude(boolean extrude) {
        this.extrude = extrude;
    }

    public void setUpperHeight(float upperHeight) {
        this.upperHeight = upperHeight;
    }

    public void setDepthCheck(boolean depthCheck) {
        this.depthCheck = depthCheck;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegionSnapshot)) return false;
        RegionSnapshot that = (RegionSnapshot) o;
        return Float.compare(that.getHeight(), getHeight()) == 0 &&
                isExtrude() == that.isExtrude() &&
                Float.compare(that.getUpperHeight(), getUpperHeight()) == 0 &&
                getDepthCheck() == that.getDepthCheck() &&
                Double.compare(that.getMinDistance(), getMinDistance()) == 0 &&
                Double.compare(that.getMaxDistance(), getMaxDistance()) == 0 &&
                getAddon().equals(that.getAddon()) &&
                getId().equals(that.getId()) &&
                htmlDisplay.equals(that.htmlDisplay) &&
                getWorld().equals(that.getWorld()) &&
                getPoints().equals(that.getPoints()) &&
                getColor().equals(that.getColor()) &&
                getBorderColor().equals(that.getBorderColor());
    }

    public boolean refersSameRegion(RegionSnapshot other) {
        if (other == null)
            return false;
        if (other == this)
            return true;
        return other.getAddon().equals(this.getAddon()) && other.getId().equals(this.getId());
    }

    public enum State {
        UNCHANGED,
        CHANGED_OR_ADDED,
        DELETED
    }

}
