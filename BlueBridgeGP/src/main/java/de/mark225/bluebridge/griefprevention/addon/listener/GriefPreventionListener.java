package de.mark225.bluebridge.griefprevention.addon.listener;

import de.mark225.bluebridge.griefprevention.BlueBridgeGP;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimExtendEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimModifiedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GriefPreventionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimCreated(ClaimCreatedEvent e){
        if(e.isCancelled()) return;
        update(e.getClaim());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimModified(ClaimModifiedEvent e){
        if(e.isCancelled()) return;
        update(e.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimExend(ClaimExtendEvent e){
        if(e.isCancelled()) return;
        scheduleUpdate(e.getClaim());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimDelete(ClaimDeletedEvent e){
        delete(e.getClaim());
    }

    public void update(Claim claim){
        BlueBridgeGP.getInstance().getGPIntegration().addOrUpdateClaim(claim);
    }

    public void delete(Claim claim){
        BlueBridgeGP.getInstance().getGPIntegration().removeClaim(claim);
    }

    public void scheduleUpdate(Claim claim){
        Bukkit.getScheduler().runTaskLater(BlueBridgeGP.getInstance(), () ->{
            BlueBridgeGP.getInstance().getGPIntegration().addOrUpdateClaim(claim);
        }, 0l);
    }

}
