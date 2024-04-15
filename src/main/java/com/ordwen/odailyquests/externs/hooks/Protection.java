package com.ordwen.odailyquests.externs.hooks;

import com.ordwen.odailyquests.tools.PluginLogger;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Protection {

    private static boolean isTownyEnabled = false;

    public void load() {
        if (Bukkit.getPluginManager().isPluginEnabled("Towny")) {
            setTownyEnabled(true);
            PluginLogger.info("Towny detected, hook enabled.");
        }
    }

    public static boolean isTownyEnabled() {
        return isTownyEnabled;
    }

    public static void setTownyEnabled(boolean townyEnabled) {
        isTownyEnabled = townyEnabled;
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
        if (!isTownyEnabled()) return true;

        // Towny check
        return PlayerCacheUtil.getCachePermission(player, block.getLocation(), block.getType(), TownyPermission.ActionType.BUILD);
    }
}
