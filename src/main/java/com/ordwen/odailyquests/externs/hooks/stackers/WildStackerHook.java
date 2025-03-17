package com.ordwen.odailyquests.externs.hooks.stackers;

import org.bukkit.Bukkit;

public class WildStackerHook {

    private WildStackerHook() {}

    /**
     * Check if WildStacker is enabled.
     * @return true if WildStacker is enabled.
     */
    public static boolean isEnabled() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("WildStacker");
    }
}
