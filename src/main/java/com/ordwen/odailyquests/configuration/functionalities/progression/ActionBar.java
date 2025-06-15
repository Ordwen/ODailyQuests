package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.PluginLogger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActionBar implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public ActionBar(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;
    private String text;

    @Override
    public void load() {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("actionbar");

        if (section == null) {
            PluginLogger.error("Actionbar section is missing in the configuration file. Disabling.");
            isEnabled = false;
            return;
        }

        isEnabled = section.getBoolean("enabled");
        if (isEnabled) text = TextFormatter.format(section.getString("text"));
    }

    public void sendActionbarInternal(Player player, String questName) {
        if (isEnabled) {
            final String parsedQuestName = TextFormatter.format(player, questName);

            final String playerBar = TextFormatter.format(player, this.text
                    .replace("%player%", player.getDisplayName())
                    .replace("%questName%", parsedQuestName));

            final String toSend = TextFormatter.format(playerBar);
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(toSend));
        }
    }

    private static ActionBar getInstance() {
        return ConfigFactory.getConfig(ActionBar.class);
    }

    public static void sendActionbar(Player player, String questName) {
        getInstance().sendActionbarInternal(player, questName);
    }
}
