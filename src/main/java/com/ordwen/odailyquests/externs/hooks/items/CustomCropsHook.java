package com.ordwen.odailyquests.externs.hooks.items;

import org.bukkit.Bukkit;

public class CustomCropsHook {

    /**
     * Check if CustomCrops is enabled.
     * @return true if EliteMobs is enabled.
     */
    public static boolean isCustomCropsSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("CustomCrops");
    }
}
