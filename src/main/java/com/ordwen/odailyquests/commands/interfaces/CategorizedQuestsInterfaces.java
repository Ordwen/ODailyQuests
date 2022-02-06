package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Logger;

public class CategorizedQuestsInterfaces {

    /**
     * Getting instance of classes.
     */
    private final ConfigurationFiles configurationFiles;

    /**
     * Class instance constructor.
     * @param configurationFiles configuration files class.
     */
    public CategorizedQuestsInterfaces(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("ODailyQuests");

    /**
     * Load categorized quests interfaces.
     */
    public void loadCategorizedQuestsInterfaces() {
        logger.info(ChatColor.GREEN + "Categorized quests interfaces successfully loaded.");
    }
}
