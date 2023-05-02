package com.ordwen.odailyquests.externs.hooks.mobs;

import org.bukkit.Bukkit;

public class MythicMobsHook {

    /**
     * Check if MythicMobs is enabled.
     * @return true if MythicMobs is enabled.
     */
    public static boolean isMythicMobsSetup() {
         return Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs");
    }
}
