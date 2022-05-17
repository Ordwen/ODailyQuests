package com.ordwen.odailyquests.configuration.functions;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Actionbar {

    private ConfigurationFiles configurationFiles;

    public Actionbar(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Init variables
     */
    private static boolean isEnabled;
    private static TextComponent text;

    /**
     * Load configuration section.
     */
    public void loadActionbar() {
        ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("actionbar");
        isEnabled = section.getBoolean("enabled");

        if (isEnabled) {
            text = new TextComponent(section.getString("text"));
        }
    }

    /**
     * Send actionbar.
     * @param player to send.
     * @param questName name of the achieved quest.
     */
    public static void sendActionbar(Player player, String questName) {
        TextComponent toSend = new TextComponent(text.getText().replace("%player%", player.getDisplayName()).replace("%questName%", questName));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, toSend);
    }
}
