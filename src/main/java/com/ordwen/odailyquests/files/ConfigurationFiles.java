package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.enums.QuestsMessages;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
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
    Logger logger = PluginLogger.getLogger("O'DailyQuests");

    private FileConfiguration config;
    private FileConfiguration messages;

    private static FileConfiguration LANG;
    private static File LANG_FILE;

    /**
     * Get the configuration file.
     * @return config file.
     */
    public FileConfiguration getConfigFile() {
        return this.config;
    }

    /**
     * Get the messages file configuration.
     * @return messages file configuration.
     */
    public FileConfiguration getMessagesFileConfiguration() {
        return LANG;
    }

    /**
     * Get the messages file.
     * @return messages file.
     */
    public File getMessagesFile() {
        return LANG_FILE;
    }

    /**
     * Init configuration files.
     */
    public void loadConfigurationFiles() {

        File configFile = new File(oDailyQuests.getDataFolder(), "config.yml");

        /* Configuration file */
        if (!configFile.exists()) {
            oDailyQuests.saveResource("config.yml", false);
            logger.info(ChatColor.GREEN + "Config file created.");
        }

        config = new YamlConfiguration();

        /* Configuration file */
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occurred on the load of the configuration file.");
            logger.info(ChatColor.RED + "Please inform the developer.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Configuration file successfully loaded.");
    }

    /**
     * Init messages files.
     */
    public void loadMessagesFiles() {

        File messagesFile = new File(oDailyQuests.getDataFolder(), "messages.yml");

        /* Messages file */
        if (!messagesFile.exists()) {
            oDailyQuests.saveResource("messages.yml", false);
            logger.info(ChatColor.GREEN + "Messages file created.");
        }

        messages = new YamlConfiguration();

        /* Messages file */
        try {
            messages.load(messagesFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occurred on the load of the messages file.");
            logger.info(ChatColor.RED + "Please inform the developer.");
            e.printStackTrace();
        }

        for (QuestsMessages item : QuestsMessages.values()) {
            if (messages.getString(item.getPath()) == null) {
                messages.set(item.getPath(), item.getDefault());
            }
        }
        QuestsMessages.setFile(messages);
        LANG = messages;
        LANG_FILE = messagesFile;

        try {
            messages.save(getMessagesFile());
        } catch(IOException e) {
            logger.info(ChatColor.RED + "An error happened on the save of the messages file.");
            logger.info(ChatColor.RED + "If the problem persists, contact the developer.");
            e.printStackTrace();
        }

        logger.info(ChatColor.GREEN + "Messages file successfully loaded.");
    }
}
