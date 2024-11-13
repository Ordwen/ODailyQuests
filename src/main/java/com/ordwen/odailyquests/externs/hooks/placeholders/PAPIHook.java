package com.ordwen.odailyquests.externs.hooks.placeholders;

import com.ordwen.odailyquests.tools.ColorConvert;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PAPIHook {

    protected static boolean isPlaceholderAPIHooked = false;

    /**
     * Check if PlaceholderAPI is enabled.
     *
     * @return true if enabled, false otherwise.
     */
    public static boolean isPlaceholderAPIHooked() {
        return isPlaceholderAPIHooked;
    }

    /**
     * Set if PlaceholderAPI is hooked
     *
     * @param b boolean
     */
    public static void setPlaceholderAPIHooked(boolean b) {
        isPlaceholderAPIHooked = b;
    }

    /**
     * Replace placeholders if PAPI is hooked.
     *
     * @param player player
     * @param str    string to replace
     * @return string with replaced placeholders if PAPI is hooked, original string otherwise
     */
    public static String getPlaceholders(Player player, String str) {
        if (isPlaceholderAPIHooked) return (ColorConvert.convertColorCode(PlaceholderAPI.setPlaceholders(player, str)));
        else return str;
    }
}
