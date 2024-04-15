package com.ordwen.odailyquests.externs.hooks;

import com.ordwen.odailyquests.tools.PluginLogger;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Protection {

    private static boolean isTownyEnabled = false;
    private static boolean isWorldguardEnabled = false;

    public void load() {
        if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            setTownyEnabled(true);
            PluginLogger.info("Towny detected, hook enabled.");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            setWorldGuardEnabled(true);
            PluginLogger.info("WorldGuard detected, hook enabled.");
        }
    }

    public static boolean isTownyEnabled() {
        return isTownyEnabled;
    }

    public static void setTownyEnabled(boolean enabled) {
        isTownyEnabled = enabled;
    }

    public static boolean isWorldGuardEnabled() {
        return isWorldguardEnabled;
    }

    public static void setWorldGuardEnabled(boolean enabled) {
        isWorldguardEnabled = enabled;
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

        // Towny check
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.BUILD);

        // WorldGuard check
    }

    /**
     * Check if the player can build at the location.
     *
     * @param player   the player
     * @param world    the world
     * @param location the location
     * @return true if the player can build, false otherwise
     */
    private static boolean checkWg(Player player, World world, Location location) {
        if (!isWorldGuardEnabled()) return true;

        final com.sk89q.worldedit.util.Location adaptedLocation = BukkitAdapter.adapt(location);
        final com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        if (wgPlatform.getSessionManager().hasBypass(localPlayer, adaptedWorld)) return true;

        final RegionQuery query = wgPlatform.getRegionContainer().createQuery();
        return query.testBuild(adaptedLocation, localPlayer, Flags.BUILD);
    }
}
