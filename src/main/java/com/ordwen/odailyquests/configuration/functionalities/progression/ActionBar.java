package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionBar {

    /**
     * Init variables
     */
    private static boolean isEnabled;
    private static String text;
    private final ConfigurationFiles configurationFiles;
    public ActionBar(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Send actionbar.
     *
     * @param player    to send.
     * @param questName name of the achieved quest.
     */
    public static void sendActionbar(final Player player, final String questName) {
        if (isEnabled) {

            final String toSend = ColorConvert.convertColorCode(text
                    .replace("%player%", player.getDisplayName())
                    .replace("%questName%", questName)
            );

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(toSend));
        }
    }

    /**
     * Load configuration section.
     */
    public void loadActionbar() {
        final ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("actionbar");
        isEnabled = section.getBoolean("enabled");

        if (isEnabled) {
            text = ColorConvert.convertColorCode(ChatColor.translateAlternateColorCodes('&', section.getString("text")));
        }
    }
}
