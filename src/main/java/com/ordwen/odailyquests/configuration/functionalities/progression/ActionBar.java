package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import com.ordwen.odailyquests.files.ConfigurationFile;
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
        final ConfigurationSection section = configurationFile.getConfigFile().getConfigurationSection("actionbar");

        if (section == null) {
            PluginLogger.error("Actionbar section is missing in the configuration file. Disabling.");
            isEnabled = false;
            return;
        }

        isEnabled = section.getBoolean("enabled");
        if (isEnabled) text = ColorConvert.convertColorCode(section.getString("text"));
    }

    public void sendActionbarInternal(Player player, String questName) {
        if (isEnabled) {
            final String toSend = ColorConvert.convertColorCode(text
                    .replace("%player%", player.getDisplayName())
                    .replace("%questName%", questName)
            );
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
