package com.ordwen.odailyquests.externs.hooks.mobs;

import com.ordwen.odailyquests.tools.PluginUtils;

public class MythicMobsHook {

    private MythicMobsHook() {}

    /**
     * Check if MythicMobs is enabled.
     * @return true if MythicMobs is enabled.
     */
    public static boolean isEnabled() {
         return PluginUtils.isPluginEnabled("MythicMobs");
    }
}
