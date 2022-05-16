package com.ordwen.odailyquests.configuration;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Title {

    private final ConfigurationFiles configurationFiles;

    public Title(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Init variables
     */
    private static boolean isEnabled;
    private static int fadeIn;
    private static int fadeOut;
    private static int stay;
    private static String title;
    private static String subtitle;

    /**
     * Load configuration section.
     */
    public void loadTitle() {
        ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("title");
        isEnabled = section.getBoolean("enabled");

        if (isEnabled) {
            fadeIn = section.getInt("fadeIn");
            stay = section.getInt("stay");
            fadeOut = section.getInt("fadeOut");
            title = ColorConvert.convertColorCode(ChatColor.translateAlternateColorCodes('&', section.getString( "text")));
            subtitle = ColorConvert.convertColorCode(ChatColor.translateAlternateColorCodes('&', section.getString("subtitle")));

            PluginLogger.info("Title successfully loaded.");
        } else PluginLogger.info("Title is disabled.");
    }

    /**
     * Send title to player.
     * @param player player to send the title.
     * @param questName name of the achieved quest.
     */
    public static void sendTitle(Player player, String questName) {
        if (isEnabled) {
            player.sendTitle(
                    title.replace("%player%", player.getDisplayName()).replace("%questName%", questName),
                    subtitle.replace("%player%", player.getDisplayName()).replace("%questName%", questName),
                    fadeIn, stay, fadeOut);
        }
    }
}
