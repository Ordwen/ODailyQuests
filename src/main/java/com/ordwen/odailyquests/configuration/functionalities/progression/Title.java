package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Title implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public Title(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private boolean isEnabled;
    private int fadeIn;
    private int fadeOut;
    private int stay;
    private String mainTitle;
    private String subTitle;

    @Override
    public void load() {
        final ConfigurationSection section = configurationFiles.getConfigFile().getConfigurationSection("title");

        if (section == null) {
            PluginLogger.error("Title section is missing in the configuration file. Disabling.");
            isEnabled = false;
            return;
        }

        isEnabled = section.getBoolean("enabled");

        if (isEnabled) {
            fadeIn = section.getInt("fadeIn");
            stay = section.getInt("stay");
            fadeOut = section.getInt("fadeOut");
            mainTitle = ColorConvert.convertColorCode(section.getString("text"));
            subTitle = ColorConvert.convertColorCode(section.getString("subtitle"));

            PluginLogger.fine("Title successfully loaded.");
        } else PluginLogger.fine("Title is disabled.");
    }

    public void sendTitleInternal(Player player, String questName) {
        if (isEnabled) {
            player.sendTitle(
                    mainTitle.replace("%player%", player.getDisplayName()).replace("%questName%", questName),
                    subTitle.replace("%player%", player.getDisplayName()).replace("%questName%", questName),
                    fadeIn, stay, fadeOut);
        }
    }

    private static Title getInstance() {
        return ConfigFactory.getConfig(Title.class);
    }

    public static void sendTitle(Player player, String questName) {
        getInstance().sendTitleInternal(player, questName);
    }
}
