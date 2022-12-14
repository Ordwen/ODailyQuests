package com.ordwen.odailyquests.tools;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class GetPlaceholders {

    public static boolean isPlaceholderAPIHooked = false;

    /**
     * Replace placeholders if PAPI is hooked.
     * @param player player
     * @param str string to replace
     * @return string with replaced placeholders if PAPI is hooked, original string otherwise
     */
    public static String getPlaceholders(Player player, String str) {
        if (isPlaceholderAPIHooked) return (PlaceholderAPI.setPlaceholders(player, str));
        else return str;
    }
}
