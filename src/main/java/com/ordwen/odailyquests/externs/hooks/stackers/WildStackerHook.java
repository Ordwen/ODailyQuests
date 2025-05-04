package com.ordwen.odailyquests.externs.hooks.stackers;

import com.ordwen.odailyquests.tools.PluginUtils;

public class WildStackerHook {

    private WildStackerHook() {}

    /**
     * Check if WildStacker is enabled.
     * @return true if WildStacker is enabled.
     */
    public static boolean isEnabled() {
        return PluginUtils.isPluginEnabled("WildStacker");
    }
}
