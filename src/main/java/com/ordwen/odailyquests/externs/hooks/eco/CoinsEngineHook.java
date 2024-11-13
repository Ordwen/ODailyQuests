package com.ordwen.odailyquests.externs.hooks.eco;

import org.bukkit.Bukkit;

public class CoinsEngineHook {

    private static boolean isHooked;

    /**
     * Setup CoinsEngineAPI.
     */
    public static boolean setupCoinsEngineAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("CoinsEngine")) {
            isHooked = true;
            return true;
        }
        return false;
    }

    /**
     * Check if CoinsEngineAPI is hooked.
     *
     * @return true if CoinsEngineAPI is hooked - false if not.
     */
    public static boolean isCoinsEngineHooked() {
        return isHooked;
    }
}
