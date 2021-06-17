package de.mark225.bluebridge.griefprevention.addon.listener;

import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.griefprevention.BlueBridgeGP;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimDeletedEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimExtendEvent;
import me.ryanhamshire.GriefPrevention.events.ClaimModifiedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.logging.Level;

public class GriefPreventionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimCreated(ClaimCreatedEvent e){
        if(BlueBridgeConfig.debug()) BlueBridgeGP.getInstance().getLogger().log(Level.INFO, "Claim created " + e.getClaim().getID());
        if(e.isCancelled()) return;
        scheduleUpdate(e.getClaim());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimModified(ClaimModifiedEvent e){
        if(BlueBridgeConfig.debug()) BlueBridgeGP.getInstance().getLogger().log(Level.INFO, "Claim modified " + e.getClaim().getID());
        if(e.isCancelled()) return;
        scheduleUpdate(e.getTo());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimExend(ClaimExtendEvent e){
        if(BlueBridgeConfig.debug()) BlueBridgeGP.getInstance().getLogger().log(Level.INFO, "Claim extended " + e.getClaim().getID());
        if(e.isCancelled()) return;
        scheduleUpdate(e.getClaim());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClaimDelete(ClaimDeletedEvent e){
        if(BlueBridgeConfig.debug()) BlueBridgeGP.getInstance().getLogger().log(Level.INFO, "Claim deleted " + e.getClaim().getID());
        delete(e.getClaim());
    }

    public void delete(Claim claim){
        BlueBridgeGP.getInstance().getGPIntegration().removeClaim(claim);
    }

    public void scheduleUpdate(Claim claim){
        Bukkit.getScheduler().runTaskLater(BlueBridgeGP.getInstance(), () ->{
            BlueBridgeGP.getInstance().getGPIntegration().addOrUpdateClaim(GriefPrevention.instance.dataStore.getClaim(claim.getID()));
        }, 0l);
    }

}
