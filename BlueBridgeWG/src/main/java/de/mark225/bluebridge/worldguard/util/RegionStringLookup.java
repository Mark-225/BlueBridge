package de.mark225.bluebridge.worldguard.util;

import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.mark225.bluebridge.core.BlueBridgeCore;
import de.mark225.bluebridge.core.config.BlueBridgeConfig;
import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import de.mark225.bluebridge.core.util.StringLookupWrapper;
import de.mark225.bluebridge.worldguard.addon.WorldGuardIntegration;
import org.bukkit.Bukkit;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RegionStringLookup extends StringLookupWrapper {
    private ProtectedRegion region;
    private FlagRegistry flagReg;

    private HashMap<String, String> cache = new HashMap<>();

    public RegionStringLookup(ProtectedRegion region){
        this.region = region;
    }


    @Override
    public String lookup(String key) {
        if(cache.containsKey(key))
            return cache.get(key);
        String result = fetch(key);
        if(BlueBridgeConfig.debug()){
            Logger log = BlueBridgeCore.getInstance().getLogger();
            log.info("Region: " + region.getId() + " Key: " + key + " Value: " + result);
        }
        cache.put(key, result);
        return result;
    }

    private String fetch(String key){
        String[] args = key.split(":");
        if(args.length > 0){
            switch (args[0]) {
                case "name":
                    if(args.length == 2)
                        return getName(Boolean.parseBoolean(args[1]));
                    return getName(false);
                case "owners":
                    if (args.length == 2) {
                        return getOwners(args[1], 10);
                    } else if (args.length == 3) {
                        try {
                            return getOwners(args[1], Integer.parseInt(args[2]));
                        } catch (NumberFormatException e) {
                            return "[Invalid placeholder syntax]";
                        }
                    }
                    break;
                case "members":
                    if (args.length == 2) {
                        return getMembers(args[1], 10);
                    } else if (args.length == 3) {
                        try {
                            return getMembers(args[1], Integer.parseInt(args[2]));
                        } catch (NumberFormatException e) {
                            return "[Invalid placeholder syntax]";
                        }
                    }
                case "size":
                    if(args.length == 2){
                        if(args[1].equals("2d")){
                            return getSize(false);
                        }else if(args[1].equals("3d")){
                            return getSize(true);
                        }
                    }
                    return "[Invalid placeholder syntax]";
                case "flag":
                    if(args.length == 2){
                        return getFlagValue(args[1]);
                    }
                    return "[Invalid placeholder syntax]";
            }
        }
        return "[Unknown placeholder]";
    }

    private String getName(boolean forceId){
        String name = null;
        if(!forceId){
            name = region.getFlag(WorldGuardIntegration.DISPLAY_FLAG);
        }
        if(name == null){
            name = region.getId();
        }
        return BlueBridgeUtils.escapeHtml(name);
    }

    private String getOwners(String delimiter, int limit){
        return region.getOwners().getUniqueIds().stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).limit(limit).collect(Collectors.joining(delimiter));
    }

    private String getMembers(String delimiter, int limit){
        return region.getMembers().getUniqueIds().stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).limit(limit).collect(Collectors.joining(delimiter));
    }

    private String getSize(boolean height){
        BigInteger area2D = new BigInteger("0");
        BlockVector3 delta = region.getMaximumPoint().add(1,1,1).subtract(region.getMinimumPoint());
        if(region instanceof ProtectedCuboidRegion){
            int dX, dZ;
            dX = delta.getX();
            dZ = delta.getZ();
            area2D = area2D.add(BigInteger.valueOf(dX));
            area2D = area2D.multiply(BigInteger.valueOf(dZ));
        }else if(region instanceof ProtectedPolygonalRegion){
            List<BlockVector2> points = region.getPoints();
            for(int i = 0; i < points.size(); i++){
                BigInteger b1 = BigInteger.valueOf(points.get(i).getX()).multiply(BigInteger.valueOf(points.get((i+1) % points.size()).getZ()));
                BigInteger b2 = BigInteger.valueOf(points.get((i+1) % points.size()).getX()).multiply(BigInteger.valueOf(points.get(i).getZ()));
                area2D = area2D.add(b1.subtract(b2));
            }
            area2D = area2D.divide(BigInteger.valueOf(2l));
            area2D = area2D.abs();
        }
        if(height){
            return area2D.multiply(BigInteger.valueOf(delta.getY())).toString();
        }
        return area2D.toString();
    }

    private String getFlagValue(String flagname){
        if(flagReg == null)
            flagReg = WorldGuard.getInstance().getFlagRegistry();
        Flag flag = flagReg.get(flagname);
        if(flag == null)
            return "[flag not registered]";
        Object flagValue = region.getFlag(flag);
        if(flagValue == null){
            if(flag.getDefault() == null){
                return "[undefined]";
            }else{
                return BlueBridgeUtils.escapeHtml(flag.getDefault().toString());
            }
        }
        return BlueBridgeUtils.escapeHtml(flagValue.toString());
    }
}
