package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.tools.ColorConvert;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionBar {

    private final ConfigurationFiles configurationFiles;

    public ActionBar(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Init variables
     */
    private static boolean isEnabled;
    private static String text;

    /**
     * Load configuration section.
     */
    public void loadActionbar() {
        ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("actionbar");
        isEnabled = section.getBoolean("enabled");

        if (isEnabled) {
            text = ColorConvert.convertColorCode(ChatColor.translateAlternateColorCodes('&', section.getString("text")));
        }
    }

    /**
     * Send actionbar.
     *
     * @param player    to send.
     * @param questName name of the achieved quest.
     */
    public static void sendActionbar(Player player, String questName) {
        if (isEnabled) {

            final String toSend = ColorConvert.convertColorCode(text
                    .replace("%player%", player.getDisplayName())
                    .replace("%questName%", questName)
            );

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(toSend));
        }
    }
}
