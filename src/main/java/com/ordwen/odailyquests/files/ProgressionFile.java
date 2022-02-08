package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ProgressionFile {

    /**
     * Getting instance of main class.
     */
    private final ODailyQuests oDailyQuests;

    /**
     * Main class instance constructor.
     * @param oDailyQuests main class.
     */
    public ProgressionFile(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("O'DailyQuests");

    private File progressionFile;
    private FileConfiguration progression;

    /**
     * Get the configuration file.
     * @return config file.
     */
    public FileConfiguration getProgressionFileConfiguration() {
        return this.progression;
    }

    /**
     * Get the file.
     * @return file.
     */
    public File getProgressionFile() { return this.progressionFile; }

    /**
     * Init progression file.
     */
    public void loadProgressionFile() {

        progressionFile = new File(oDailyQuests.getDataFolder(), "progression.yml");

        if (!progressionFile.exists()) {
            oDailyQuests.saveResource("progression.yml", false);
            logger.info(ChatColor.GREEN + "Progression file created (YAML).");
        }

        progression = new YamlConfiguration();

        try {
            progression.load(progressionFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occurred on the load of the progression file.");
            logger.info(ChatColor.RED + "Please inform the developer.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Progression file successfully loaded (YAML).");
    }
}
