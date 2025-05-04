package com.ordwen.odailyquests.externs.hooks.items;

import com.ordwen.odailyquests.tools.PluginUtils;
import me.kryniowesegryderiusz.kgenerators.api.KGeneratorsAPI;
import org.bukkit.Location;

public class KGeneratorsHook {

    private KGeneratorsHook() {}

    private static boolean isHooked;

    /**
     * Setup KGeneratorsAPI.
     */
    public static boolean setupKGeneratorsAPI() {
        if (PluginUtils.isPluginEnabled("KGenerators")) {
            isHooked = true;
            return true;
        }

        return false;
    }

    /**
     * Check if location is a KGenerators generator.
     * @param location - location to check.
     * @return true if location is a KGenerators generator - false if not.
     */
    public static boolean isKGeneratorsLocation(Location location) {
        return isHooked && KGeneratorsAPI.getLoadedGeneratorLocation(location) != null;
    }
}
