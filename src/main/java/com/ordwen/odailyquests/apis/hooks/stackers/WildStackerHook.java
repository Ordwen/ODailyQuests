package com.ordwen.odailyquests.apis.hooks.stackers;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class WildStackerHook implements Listener {

    /**
     * Check if WildStacker is enabled.
     * @return true if WildStacker is enabled.
     */
    public static boolean isWildStackerSetup() {
        return Bukkit.getServer().getPluginManager().isPluginEnabled("WildStacker");
    }
}
