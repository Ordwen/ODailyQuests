package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ConfigurationFiles {

    /**
     * Getting instance of main class.
     */
    private final ODailyQuests oDailyQuests;

    /**
     * Main class instance constructor.
     * @param oDailyQuests main class.
     */
    public ConfigurationFiles(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("ODailyQuests");

    private FileConfiguration config;
    private FileConfiguration messages;

    /**
     * Get the configuration file.
     * @return config file.
     */
    public FileConfiguration getConfigFile() {
        return this.config;
    }

    /**
     * Get the messages file.
     * @return messages file.
     */
    public FileConfiguration getMessagesFile() {
        return this.messages;
    }

    /**
     * Init configuration files.
     */
    public void loadConfigurationFiles() {

        File configFile = new File(oDailyQuests.getDataFolder(), "config.yml");
        File messagesFile = new File(oDailyQuests.getDataFolder(), "messages.yml");

        /* Configuration file */
        if (!configFile.exists()) {
            oDailyQuests.saveResource("config.yml", false);
            logger.info(ChatColor.GREEN + "Config file created.");
        }

        /* Messages file */
        if (!messagesFile.exists()) {
            oDailyQuests.saveResource("messages.yml", false);
            logger.info(ChatColor.GREEN + "Messages file created.");
        }

        config = new YamlConfiguration();
        messages = new YamlConfiguration();

        /* Configuration file */
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occured on the load of the configuration file.");
            logger.info(ChatColor.RED + "Please inform the developper.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Configuration file successfully loaded.");

        /* Messages file */
        try {
            messages.load(messagesFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occured on the load of the messages file.");
            logger.info(ChatColor.RED + "Please inform the developper.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Messages file successfully loaded.");
    }
}
