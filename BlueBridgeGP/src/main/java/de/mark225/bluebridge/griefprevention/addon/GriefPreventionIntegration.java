package de.mark225.bluebridge.griefprevention.addon;

import com.flowpowered.math.vector.Vector2d;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import de.mark225.bluebridge.core.region.RegionSnapshotBuilder;
import de.mark225.bluebridge.griefprevention.BlueBridgeGP;
import de.mark225.bluebridge.griefprevention.addon.listener.GriefPreventionListener;
import de.mark225.bluebridge.griefprevention.config.BlueBridgeGPConfig;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.util.BoundingBox;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GriefPreventionIntegration{

    private HashMap<Long, RegionSnapshot> claims = new HashMap<>();

    public void init(){
        addExistingClaims();
        Bukkit.getPluginManager().registerEvents(new GriefPreventionListener(), BlueBridgeGP.getInstance());
    }

    public void reload(){
        resetClaims();
        addExistingClaims();
    }

    public HashMap<Long, RegionSnapshot> getClaims(){
        return claims;
    }

    public void addOrUpdateClaim(Claim claim){
        //First, schedule updates for all child claims
        if(claim.children != null && !claim.children.isEmpty()){
            for(Claim child : claim.children){
                Bukkit.getScheduler().runTaskLater(BlueBridgeGP.getInstance(), () ->{
                    addOrUpdateClaim(child);
                }, 0l);
            }
        }

        //Figure out claim depth (how many layers of parents are above this claim)
        int layer = 0;
        Claim currentClaim = claim;
        while (currentClaim.parent != null){
            layer++;
            currentClaim = currentClaim.parent;
        }

        BoundingBox boundingBox = trimmedBoundingBox(claim);
        List<Vector2d> points = boundingBoxToPolyCorners(boundingBox);
        String label = claim.isAdminClaim() ? BlueBridgeGPConfig.getInstance().adminDisplayName() : claim.getOwnerName() + "'s Claim";
        int claimFloor = claim.getLesserBoundaryCorner().getBlockY();
        int claimCeiling = claim.getGreaterBoundaryCorner().getWorld().getMaxHeight();
        boolean extrude = BlueBridgeGPConfig.getInstance().defaultExtrude();
        float heightModifier = 0f;
        //Adjust height if plugin is configured to layer child claims and 3D markers are off
        if(!extrude && BlueBridgeGPConfig.getInstance().layerChildren()){
            //Add 1/16 of a block per layer depth
            heightModifier = (float) layer * 0.0625f;
        }

        Color borderColor = claim.isAdminClaim() ? BlueBridgeGPConfig.getInstance().adminOutlineColor() : BlueBridgeGPConfig.getInstance().defaultOutlineColor();
        Color fillColor = claim.isAdminClaim() ? BlueBridgeGPConfig.getInstance().adminFillColor() : BlueBridgeGPConfig.getInstance().defaultColor();

        RegionSnapshot rs = new RegionSnapshotBuilder(BlueBridgeGP.getInstance().getAddon(), claim.getID().toString(), points, claim.getLesserBoundaryCorner().getWorld().getUID())
                .setHtmlDisplay(label)
                .setHeight(extrude ? claimFloor : (float) BlueBridgeGPConfig.getInstance().renderHeight() + heightModifier)
                .setExtrude(extrude)
                .setUpperHeight(claimCeiling)
                .setColor(fillColor)
                .setBorderColor(borderColor)
                .build();
        claims.put(claim.getID(), rs);
    }

    public void removeClaim(Claim claim){
        claims.remove(claim.getID());
    }

    public void resetClaims(){
        claims.clear();
    }

    public void addExistingClaims(){
        for(Claim claim : GriefPrevention.instance.dataStore.getClaims()){
            addOrUpdateClaim(claim);
        }
    }

    private BoundingBox trimmedBoundingBox(Claim claim){
        BoundingBox bb = new BoundingBox(claim);
        while(claim.parent != null){
            claim = claim.parent;
            BoundingBox parentBox = new BoundingBox(claim);
            //Failsafe for parent claims that don't overlap with their child. Should probably never happen.
            if(!parentBox.intersects(bb)) return bb;
            bb = parentBox.intersection(bb);
        }
        return bb;
    }

    private List<Vector2d> boundingBoxToPolyCorners(BoundingBox bb){
        List<Vector2d> points = new ArrayList<>();
        points.add(new Vector2d(bb.getMinX(), bb.getMinZ()));
        points.add(new Vector2d(bb.getMaxX() + 1, bb.getMinZ()));
        points.add(new Vector2d(bb.getMaxX() + 1, bb.getMaxZ() + 1));
        points.add(new Vector2d(bb.getMinX(), bb.getMaxZ() + 1));
        return points;
    }


}
