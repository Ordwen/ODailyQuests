package com.ordwen.odailyquests.externs.hooks.mobs;

import org.bukkit.Bukkit;

public class EliteMobsHook {

    /**
     * Check if EliteMobs is enabled.
     * @return true if EliteMobs is enabled.
     */
    public static boolean isEliteMobsSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("EliteMobs");
    }
}

