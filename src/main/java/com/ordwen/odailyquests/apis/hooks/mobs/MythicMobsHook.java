package com.ordwen.odailyquests.apis.hooks.mobs;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class MythicMobsHook implements Listener {

    /**
     * Check if MythicMobs is enabled.
     * @return true if MythicMobs is enabled.
     */
    public static boolean isMythicMobsSetup() {
         return Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs");
    }
}
