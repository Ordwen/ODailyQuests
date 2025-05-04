package com.ordwen.odailyquests.externs.hooks.mobs;

import com.ordwen.odailyquests.tools.PluginUtils;

public class EliteMobsHook {

    private EliteMobsHook() {}

    /**
     * Check if EliteMobs is enabled.
     * @return true if EliteMobs is enabled.
     */
    public static boolean isEnabled() {
        return PluginUtils.isPluginEnabled("EliteMobs");
    }
}

