package com.ordwen.odailyquests.tools;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter {

    private TextFormatter() {
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
    private static boolean placeholderAPIEnabled = false;

    /**
     * Enable or disable the use of PlaceholderAPI.
     *
     * @param enabled true to enable, false to disable.
     */
    public static void setPlaceholderAPIEnabled(boolean enabled) {
        placeholderAPIEnabled = enabled;
    }

    /**
     * Check if PlaceholderAPI is enabled.
     *
     * @return true if enabled, false otherwise.
     */
    public static boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    /**
     * Format a message, replacing color codes.
     *
     * @param message message to format
     */
    public static String format(String message) {
        if (message == null) return null;
        return replaceAll(message);
    }

    /**
     * Format a message for a player, replacing placeholders and color codes.
     *
     * @param player  player
     * @param message message to format
     * @return formatted message
     */
    public static String format(Player player, String message) {
        if (message == null) return null;

        if (placeholderAPIEnabled) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        message = replaceAll(message);

        return message;
    }

    /**
     * Apply color codes to a message.
     *
     * @param message message to apply color codes to
     * @return message with color codes applied
     */
    private static String replaceAll(String message) {
        message = applyHexColor(message);
        message = ChatColor.translateAlternateColorCodes('&', message);
        return message;
    }

    /**
     * Replace hex color codes with ChatColor.
     *
     * @param message message to replace
     * @return message with replaced hex color codes
     */
    private static String applyHexColor(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String hexCode = matcher.group();
            message = message.replace(hexCode, ChatColor.of(hexCode).toString());
            matcher = HEX_PATTERN.matcher(message);
        }
        return message;
    }
}
