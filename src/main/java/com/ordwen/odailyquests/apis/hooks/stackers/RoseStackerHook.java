package com.ordwen.odailyquests.apis.hooks.stackers;

import org.bukkit.Bukkit;

public class RoseStackerHook {

    /**
     * Check if RoseStacker is enabled.
     * @return true if RoseStacker is enabled.
     */
    public static boolean isRoseStackerSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("WildStacker");
    }
}
