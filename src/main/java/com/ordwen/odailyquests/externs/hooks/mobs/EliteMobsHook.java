package com.ordwen.odailyquests.externs.hooks.mobs;

import org.bukkit.Bukkit;

public class EliteMobsHook {

    private EliteMobsHook() {}

    /**
     * Check if EliteMobs is enabled.
     * @return true if EliteMobs is enabled.
     */
    public static boolean isEnabled() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("EliteMobs");
    }
}

