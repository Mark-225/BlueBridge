package de.mark225.bluebridge.worldguard.addon;

import com.flowpowered.math.vector.Vector2d;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.bluecolored.bluemap.api.math.Color;
import de.mark225.bluebridge.core.region.RegionSnapshot;
import de.mark225.bluebridge.core.region.RegionSnapshotBuilder;
import de.mark225.bluebridge.core.util.BlueBridgeUtils;
import de.mark225.bluebridge.worldguard.BlueBridgeWG;
import de.mark225.bluebridge.worldguard.config.BlueBridgeWGConfig;
import de.mark225.bluebridge.worldguard.util.RegionStringLookup;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WorldGuardIntegration {
    public static StateFlag RENDER_FLAG;
    public static StateFlag DEPTH_CHECK_FLAG;
    public static IntegerFlag HEIGHT_FLAG;
    public static StateFlag EXTRUDE_FLAG;
    public static StringFlag COLOR_FLAG;
    public static StringFlag OUTLINE_FLAG;
    public static StringFlag DISPLAY_FLAG;

    private static final Pattern hexPatternRGBA = Pattern.compile("[0-9a-f]{8}");
    private static final Pattern hexPatternRGB = Pattern.compile("[0-9a-f]{6}");

    /**
     * Initializes the Worldguard integration and registers the custom flags. Can only be called once during {@link org.bukkit.plugin.java.JavaPlugin#onLoad()}
     *
     * @return true if the registration of custom flags was successful
     */
    public boolean init() {
        //register custom flags
        FlagRegistry flags = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag renderFlag = new StateFlag("render-on-bluemap", true);
            StateFlag depthCheckFlag = new StateFlag("bluemap-depth-check", false);
            IntegerFlag heightFlag = new IntegerFlag("bluemap-render-height");
            StateFlag extrudeFlag = new StateFlag("bluemap-extrude", false);
            StringFlag colorFlag = new StringFlag("bluemap-color", Integer.toHexString(BlueBridgeUtils.colorToInt(BlueBridgeWGConfig.getInstance().defaultColor())));
            StringFlag outlineFlag = new StringFlag("bluemap-color-outline", Integer.toHexString(BlueBridgeUtils.colorToInt(BlueBridgeWGConfig.getInstance().defaultOutlineColor())).substring(2));
            StringFlag displayFlag = new StringFlag("bluemap-display");
            flags.registerAll(Arrays.asList(new Flag[]{renderFlag, depthCheckFlag, heightFlag, extrudeFlag, colorFlag, outlineFlag, displayFlag}));
            RENDER_FLAG = renderFlag;
            HEIGHT_FLAG = heightFlag;
            EXTRUDE_FLAG = extrudeFlag;
            COLOR_FLAG = colorFlag;
            OUTLINE_FLAG = outlineFlag;
            DISPLAY_FLAG = displayFlag;
            DEPTH_CHECK_FLAG = depthCheckFlag;
            return true;
        } catch (FlagConflictException e) {
            BlueBridgeWG.getInstance().getLogger().severe("Custom Worldguard flags are conflicting with another plugin!");
        }
        return false;
    }

    /**
     * Iterates through all Worldguard regions of a given world, filters out regions that can't be rendered or
     * are configured to not be rendered on Bluemap and returns them as seperate {@link RegionSnapshot} objects.
     *
     * @param worldUUID
     * @return The List of region snapshots
     */
    public List<RegionSnapshot> getAllRegions(UUID worldUUID) {

        org.bukkit.World bukkitWorld = Bukkit.getWorld(worldUUID);

        if (bukkitWorld == null) {
            BlueBridgeWG.getInstance().getLogger().warning("World " + worldUUID.toString() + " not found! Please check your Bluemap config for invalid worlds!");
            return new ArrayList<RegionSnapshot>();
        }

        World w = BukkitAdapter.adapt(bukkitWorld);
        RegionContainer regions = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager rm = regions.get(w);
        if (rm != null) {
            return rm.getRegions().values().stream().filter(pr -> {
                //filter all regions that shall not be rendered
                StateFlag.State state = pr.getFlag(RENDER_FLAG);
                return pr.getPoints().size() >= 3 && (((state == null) && BlueBridgeWGConfig.getInstance().defaultRender()) || (state == StateFlag.State.ALLOW));
            }).map(pr -> {
                //Convert polygon points to Verctor2d List
                List<Vector2d> points = getPointsForRegion(pr);
                //Convert color flags to Color Objects, if applicable
                String color = pr.getFlag(COLOR_FLAG);
                Color colorRGBA = null;
                if (color != null && hexPatternRGBA.matcher(color).matches()) {
                    int a, r, g, b;
                    a = Integer.parseInt(color.substring(0, 2), 16);
                    r = Integer.parseInt(color.substring(2, 4), 16);
                    g = Integer.parseInt(color.substring(4, 6), 16);
                    b = Integer.parseInt(color.substring(6, 8), 16);
                    int rgba = (((((a << 8) + r) << 8) + g) << 8) + b;
                    colorRGBA = new Color(rgba);
                } else {
                    colorRGBA = BlueBridgeWGConfig.getInstance().defaultColor();
                }
                String bordercolor = pr.getFlag(OUTLINE_FLAG);
                Color colorRGB = null;
                if (bordercolor != null && hexPatternRGB.matcher(bordercolor).matches()) {
                    colorRGB = new Color(Integer.parseInt(bordercolor, 16), 1);
                } else {
                    colorRGB = BlueBridgeWGConfig.getInstance().defaultOutlineColor();
                }
                StateFlag.State depthCheckVal = pr.getFlag(DEPTH_CHECK_FLAG);
                boolean depthCheck = depthCheckVal != null ? depthCheckVal == StateFlag.State.ALLOW : BlueBridgeWGConfig.getInstance().defaultDepthCheck();

                StateFlag.State extrudeVal = pr.getFlag(EXTRUDE_FLAG);
                boolean extrude = extrudeVal != null ? extrudeVal == StateFlag.State.ALLOW : BlueBridgeWGConfig.getInstance().defaultExtrude();

                int height = pr.getFlag(HEIGHT_FLAG) != null ? pr.getFlag(HEIGHT_FLAG) : BlueBridgeWGConfig.getInstance().renderHeight();

                //create and return new RegionSnapshot

                return new RegionSnapshotBuilder(BlueBridgeWG.getInstance().getAddon(), pr.getId(), points, worldUUID)
                        .setHtmlDisplay(parseHtmlDisplay(pr))
                        .setShortName(pr.getId())
                        .setHeight(extrude ? pr.getMinimumPoint().getBlockY() : height)
                        .setExtrude(extrude)
                        .setUpperHeight(pr.getMaximumPoint().getBlockY() + 1)
                        .setDepthCheck(depthCheck)
                        .setColor(colorRGBA)
                        .setBorderColor(colorRGB)
                        .build();
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private String parseHtmlDisplay(ProtectedRegion region) {
        return BlueBridgeUtils.replace(new RegionStringLookup(region), BlueBridgeWGConfig.getInstance().htmlPreset());
    }

    private List<Vector2d> getPointsForRegion(ProtectedRegion region) {
        if (region instanceof ProtectedCuboidRegion) {
            BlockVector3 blockVectorMin = region.getMinimumPoint();
            BlockVector3 blockVectorMax = region.getMaximumPoint();
            Vector2d min = new Vector2d(blockVectorMin.getX(), blockVectorMin.getZ());
            Vector2d max = new Vector2d(blockVectorMax.getX(), blockVectorMax.getZ());
            List<Vector2d> list = new ArrayList<>();
            list.add(min);
            list.add(new Vector2d(max.getX() + 1, min.getY()));
            list.add(new Vector2d(max.getX() + 1, max.getY() + 1));
            list.add(new Vector2d(min.getX(), max.getY() + 1));
            return list;
        }
        return region.getPoints().stream().map(bv2 -> new Vector2d(bv2.getX() + 0.5, bv2.getZ() + 0.5)).collect(Collectors.toList());
    }

    private int polygonArea(List<BlockVector2> coordinates) {
        int size = coordinates.size();
        int sum = 0;
        for (int i = 0; i < size; i++) {
            sum += (coordinates.get(i).getX() * coordinates.get((i + 1) % size).getZ()) - (coordinates.get(i).getZ() * coordinates.get((i + 1) % size).getX());
        }
        return Math.abs((int) ((double) sum / 2d));
    }

}
