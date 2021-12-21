package de.mark225.bluebridge.griefprevention.util;

import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import de.mark225.bluebridge.core.util.StringLookupWrapper;
import me.ryanhamshire.GriefPrevention.Claim;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.*;
import java.util.stream.Collectors;

public class ClaimStringLookup extends StringLookupWrapper {

    private final Claim claim;
    private final HashMap<String, String> cache = new HashMap<>();

    private final HashMap<String, List<String>> trusts = new HashMap<>();

    public ClaimStringLookup(Claim claim){
        this.claim = claim;
    }

    @Override
    public String replace(String in) {
        String cached = cache.get(in);
        if(cached != null)
            return cached;
        String fetched = fetch(in);
        cache.put(in, fetched);
        return fetched;
    }

    private String fetch(String in){
        String[] args = in.split(":");
        if(args.length <= 0 )
            return "[unknown placeholder]";

        switch(args[0].toLowerCase()){
            case "owner":
                if(args.length > 2) break;
                return getOwner(args.length == 2 ? args[1] : "Admin");
            case "size":
                if(args.length > 2) break;
                return getSize(args.length != 1 && Boolean.parseBoolean(args[1]));
            case "trustlist":
                if(args.length != 5) break;
                try {
                    return getNameList(args[1], args[2], Integer.parseInt(args[3]), Boolean.parseBoolean(args[4]));
                }catch(NumberFormatException e){
                    return "[Invalid number format! Check config]";
                }
        }

        return "[unknown placeholder]";
    }

    private String getOwner(String adminOverride){
        return BlueBridgeUtils.escapeHtml(claim.isAdminClaim() ? adminOverride : claim.getOwnerName());
    }

    private String getSize(boolean volume){
        return "" +  (volume ? (claim.getArea() * claim.getHeight()) : claim.getArea());
    }

    private String getNameList(String type, String delimiter, int limit, boolean showRemaining){
        initTrustLists();
        if(!trusts.containsKey(type))
            return "[unknown trust level]";
        return uuidsToNames(trusts.get(type), delimiter, limit, showRemaining);
    }

    private String uuidsToNames(Collection<String> uuidList, String delimiter, int limit, boolean showRemaining){
        String names = uuidList.stream().limit(limit).map(this::resolveName).collect(Collectors.joining(delimiter));
        if(limit < uuidList.size() && showRemaining)
            names = names + delimiter + "+" + (uuidList.size() - limit) + " more";
        return names;
    }

    private String resolveName(String uuid){
        try {
            return resolveName(UUID.fromString(uuid));
        }catch(IllegalArgumentException e){
            return BlueBridgeUtils.escapeHtml("n/a");
        }
    }
    private String resolveName(UUID uuid){
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        String name = op.getName();
        return BlueBridgeUtils.escapeHtml(name != null ? name : "n/a");
    }

    private void initTrustLists(){
        if(!trusts.isEmpty())
            return;
        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);
        trusts.put("builders", builders);
        trusts.put("containers", containers);
        trusts.put("accessors", accessors);
        trusts.put("managers", managers);
    }


}
