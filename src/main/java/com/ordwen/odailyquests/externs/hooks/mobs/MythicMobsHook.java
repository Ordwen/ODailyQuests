package com.ordwen.odailyquests.externs.hooks.mobs;

import org.bukkit.Bukkit;

public class MythicMobsHook {

    private MythicMobsHook() {}

    /**
     * Check if MythicMobs is enabled.
     * @return true if MythicMobs is enabled.
     */
    public static boolean isEnabled() {
         return Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs");
    }
}
