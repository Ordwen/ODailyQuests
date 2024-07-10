package com.ordwen.odailyquests.externs.hooks;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public class Protection {

    private static boolean isTownyEnabled = false;

    private static boolean isWorldguardEnabled = false;
    private static WorldGuardPlatform wgPlatform = null;

    public void load() {
        if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            setTownyEnabled();
            PluginLogger.info("Towny detected, hook enabled.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            setWorldGuardPlatform(WorldGuard.getInstance().getPlatform());
            setWorldGuardEnabled();
            PluginLogger.info("WorldGuard detected, hook enabled.");
        }
    }

    /**
     * Check if Towny is enabled.
     *
     * @return true if Towny is enabled, false otherwise
     */
    public static boolean isTownyEnabled() {
        return isTownyEnabled;
    }

    /**
     * Set Towny enabled.
     */
    public static void setTownyEnabled() {
        isTownyEnabled = true;
    }

    /**
     * Set the WorldGuard platform.
     *
     * @param platform WorldGuardPlatform instance
     */
    private static void setWorldGuardPlatform(WorldGuardPlatform platform) {
        wgPlatform = platform;
    }

    /**
     * Check if WorldGuard is enabled.
     *
     * @return true if WorldGuard is enabled, false otherwise
     */
    public static boolean isWorldGuardEnabled() {
        return isWorldguardEnabled;
    }

    /**
     * Set WorldGuard enabled.
     */
    public static void setWorldGuardEnabled() {
        isWorldguardEnabled = true;
    }

    /**
     * Apply all checks related to world building permissions.
     * Currently supporting Towny.
     *
     * @param player involved player.
     * @param block  involved block.
     * @return true if the player can build, false otherwise.
     */
    public static boolean canBuild(Player player, Block block, String flag) {
        Debugger.addDebug("Protection: canBuild summoned.");
        if (!isTownyEnabled() && !isWorldGuardEnabled()) {
            Debugger.addDebug("Protection: canBuild no hooks enabled.");
            return true;
        }

        return checkTowny(player, block) && checkWg(player, block, flag);
    }

    /**
     * Check if the player can build at the location using Towny.
     *
     * @param player the player
     * @param block  the block
     * @return true if the player can build, false otherwise
     */
    private static boolean checkTowny(Player player, Block block) {
        if (!isTownyEnabled()) return true;

        Debugger.addDebug("Protection: checkTowny summoned.");

        final Location location = block.getLocation();
        final boolean canBuild = PlayerCacheUtil.getCachePermission(player, location, block.getType(), TownyPermission.ActionType.BUILD);
        Debugger.addDebug("Protection: checkTowny result: " + canBuild);

        return canBuild;
    }

    /**
     * Check if the player can build at the location.
     *
     * @param player the player
     * @param block  the block
     * @return true if the player can build, false otherwise
     */
    public static boolean checkWg(Player player, Block block, String flag) {
        if (!isWorldGuardEnabled()) return true;

        Debugger.addDebug("Protection: checkWg summoned.");

        final Location location = block.getLocation();
        if (location.getWorld() == null) return true;

        final com.sk89q.worldedit.util.Location adaptedLocation = BukkitAdapter.adapt(location);
        final com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(location.getWorld());
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (wgPlatform.getSessionManager().hasBypass(localPlayer, adaptedWorld)) {
            Debugger.addDebug("Protection: checkWg bypassed.");
            return true;
        }

        final RegionQuery query = wgPlatform.getRegionContainer().createQuery();
        final StateFlag stateFlag = switch (flag) {
            case "BLOCK_BREAK" -> Flags.BLOCK_BREAK;
            case "BLOCK_PLACE" -> Flags.BLOCK_PLACE;
            default -> Flags.BUILD;
        };

        final boolean canBuild = query.testBuild(adaptedLocation, localPlayer, stateFlag);
        Debugger.addDebug("Protection: checkWg result: " + canBuild);

        return canBuild;
    }

    /**
     * Check if the player is in the required region to progress the quest.
     *
     * @param player         involved player
     * @param requiredRegions required regions
     * @return true if the player is in the required region, false otherwise
     */
    public static boolean checkRegion(Player player, List<String> requiredRegions) {
        if (!isWorldGuardEnabled()) return true;

        Debugger.addDebug("Protection: checkRegion summoned.");

        final Location location = player.getLocation();
        if (location.getWorld() == null) return true;

        final com.sk89q.worldedit.util.Location adaptedLocation = BukkitAdapter.adapt(location);

        final RegionContainer container = wgPlatform.getRegionContainer();
        final RegionQuery query = container.createQuery();
        final ApplicableRegionSet regions = query.getApplicableRegions(adaptedLocation);

        for (String region : requiredRegions) {
            if (regions.size() == 0) return false;

            if (regions.getRegions().stream().noneMatch(r -> r.getId().equalsIgnoreCase(region))) {
                return false;
            }
        }

        return true;
    }
}
