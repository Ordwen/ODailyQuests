package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Title {

    /**
     * Init variables
     */
    private static boolean isEnabled;
    private static int fadeIn;
    private static int fadeOut;
    private static int stay;
    private static String title;
    private static String subtitle;
    private final ConfigurationFiles configurationFiles;
    public Title(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Send title to player.
     *
     * @param player    player to send the title.
     * @param questName name of the achieved quest.
     */
    public static void sendTitle(final Player player, final String questName) {
        if (isEnabled) {
            player.sendTitle(
                    title.replace("%player%", player.getDisplayName()).replace("%questName%", questName),
                    subtitle.replace("%player%", player.getDisplayName()).replace("%questName%", questName),
                    fadeIn, stay, fadeOut);
        }
    }

    /**
     * Load configuration section.
     */
    public void loadTitle() {
        final ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("title");
        isEnabled = section.getBoolean("enabled");

        if (isEnabled) {
            fadeIn = section.getInt("fadeIn");
            stay = section.getInt("stay");
            fadeOut = section.getInt("fadeOut");
            title = ColorConvert.convertColorCode(ChatColor.translateAlternateColorCodes('&', section.getString("text")));
            subtitle = ColorConvert.convertColorCode(ChatColor.translateAlternateColorCodes('&', section.getString("subtitle")));

            PluginLogger.fine("Title successfully loaded.");
        } else PluginLogger.fine("Title is disabled.");
    }
}
