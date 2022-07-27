package com.ordwen.odailyquests.apis.hooks.mobs;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class EliteMobsHook implements Listener {

    /**
     * Check if EliteMobs is enabled.
     * @return true if EliteMobs is enabled.
     */
    public static boolean isEliteMobsSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("EliteMobs");
    }
}

