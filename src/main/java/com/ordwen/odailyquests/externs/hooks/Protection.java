package com.ordwen.odailyquests.externs.hooks;

import com.ordwen.odailyquests.tools.PluginLogger;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
    public static boolean canBuild(Player player, Block block) {
        if (!isTownyEnabled() && !isWorldGuardEnabled()) return true;

        return checkTowny(player, block) && checkWg(player, block);
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

        final Location location = block.getLocation();
        return PlayerCacheUtil.getCachePermission(player, location, block.getType(), TownyPermission.ActionType.BUILD);
    }

    /**
     * Check if the player can build at the location.
     *
     * @param player the player
     * @param block  the block
     * @return true if the player can build, false otherwise
     */
    public static boolean checkWg(Player player, Block block) {
        if (!isWorldGuardEnabled()) return true;

        final Location location = block.getLocation();
        if (location.getWorld() == null) return true;

        final com.sk89q.worldedit.util.Location adaptedLocation = BukkitAdapter.adapt(location);
        final com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(location.getWorld());
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (wgPlatform.getSessionManager().hasBypass(localPlayer, adaptedWorld)) return true;

        final RegionQuery query = wgPlatform.getRegionContainer().createQuery();
        return query.testBuild(adaptedLocation, localPlayer, Flags.BUILD);
    }
}
