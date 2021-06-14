package de.mark225.bluebridge.core.region;

import com.flowpowered.math.vector.Vector2d;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Class that stores all needed Information about a Worldguard region, so it can be accessed asynchronously
 */
public class RegionSnapshot {
    private String addon;
    private String id;
    private String htmlDisplay;
    private UUID world;
    private float height;
    private boolean extrude;
    private float upperHeight;
    private boolean depthCheck;
    private List<Vector2d> points;
    private Color color;
    private Color borderColor;
    private double minDistance;
    private double maxDistance;

    public RegionSnapshot(String addon, String id, List<Vector2d> points, UUID world){
        this.addon = addon;
        this.id = id;
        this.points = points;
        this.world = world;
    }

    public RegionSnapshot(String addon, String id, String htmlDisplay, UUID world, float height, boolean extrude, float upperHeight, boolean depthCheck, List<Vector2d> points, Color color, Color borderColor, double minDistance, double maxDistance) {
        this.addon = addon;
        this.id = id;
        this.htmlDisplay = htmlDisplay;
        this.world = world;
        this.height = height;
        this.extrude = extrude;
        this.upperHeight = upperHeight;
        this.depthCheck = depthCheck;
        this.points = points;
        this.color = color;
        this.borderColor = borderColor;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public String getAddon(){
        return addon;
    }

    public String getId() {
        return id;
    }

    public float getHeight() {
        return height;
    }

    public boolean isExtrude(){
        return extrude;
    }

    public float getUpperHeight(){
        return upperHeight;
    }

    public boolean getDepthCheck() {
        return depthCheck;
    }

    public String getName() {
        return htmlDisplay;
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

    public boolean refersSameRegion(RegionSnapshot other){
        if(other == null)
            return false;
        if(other == this)
            return true;
        return other.getAddon().equals(this.getAddon()) && other.getId().equals(this.getId());
    }

    public enum State{
        UNCHANGED,
        CHANGED_OR_ADDED,
        DELETED
    }

}
