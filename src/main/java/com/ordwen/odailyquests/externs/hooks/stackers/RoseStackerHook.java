package com.ordwen.odailyquests.externs.hooks.stackers;

import com.ordwen.odailyquests.tools.PluginUtils;

public class RoseStackerHook {

    private RoseStackerHook() {}

    /**
     * Check if RoseStacker is enabled.
     * @return true if RoseStacker is enabled.
     */
    public static boolean isRoseStackerSetup() {
        return PluginUtils.isPluginEnabled("WildStacker");
    }
}
