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

public class QuestsFiles {

    /**
     * Getting instance of main class.
     */
    private final ODailyQuests oDailyQuests;

    /**
     * Main class instance constructor.
     * @param oDailyQuests main class.
     */
    public QuestsFiles(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /* Logger for stacktrace */
    Logger logger = PluginLogger.getLogger("O'DailyQuests");

    private FileConfiguration globalQuests;
    private FileConfiguration easyQuests;
    private FileConfiguration mediumQuests;
    private FileConfiguration hardQuests;

    /**
     * Get the global quests file.
     * @return global quests file.
     */
    public FileConfiguration getGlobalQuestsFile() {
        return this.globalQuests;
    }

    /**
     * Get the easy quests file.
     * @return easy quests file.
     */
    public FileConfiguration getEasyQuestsFile() {
        return this.easyQuests;
    }

    /**
     * Get the medium quests file.
     * @return medium quests file.
     */
    public FileConfiguration getMediumQuestsFile() {
        return this.mediumQuests;
    }

    /**
     * Get the hard quests file.
     * @return hard quests file.
     */
    public FileConfiguration getHardQuestsFile() {
        return this.hardQuests;
    }

    /**
     * Init quests files.
     */
    public void loadQuestsFiles() {

        File globalQuestsFile = new File(oDailyQuests.getDataFolder(), "quests/globalQuests.yml");
        File easyQuestsFile = new File(oDailyQuests.getDataFolder(), "quests/easyQuests.yml");
        File mediumQuestsFile = new File(oDailyQuests.getDataFolder(), "quests/mediumQuests.yml");
        File hardQuestsFile = new File(oDailyQuests.getDataFolder(), "quests/hardQuests.yml");

        /* Global quests */
        if (!globalQuestsFile.exists()) {
            oDailyQuests.saveResource("quests/globalQuests.yml", false);
            logger.info(ChatColor.GREEN + "Global quests file created.");
        }

        /* Easy quests */
        if (!easyQuestsFile.exists()) {
            oDailyQuests.saveResource("quests/easyQuests.yml", false);
            logger.info(ChatColor.GREEN + "Easy quests file created.");
        }

        /* Medium quests */
        if (!mediumQuestsFile.exists()) {
            oDailyQuests.saveResource("quests/mediumQuests.yml", false);
            logger.info(ChatColor.GREEN + "Medium quests file created.");
        }

        /* Hard quests */
        if (!hardQuestsFile.exists()) {
            oDailyQuests.saveResource("quests/hardQuests.yml", false);
            logger.info(ChatColor.GREEN + "Hard quests file created.");
        }

        globalQuests = new YamlConfiguration();
        easyQuests = new YamlConfiguration();
        mediumQuests = new YamlConfiguration();
        hardQuests = new YamlConfiguration();

        /* Global quests */
        try {
            globalQuests.load(globalQuestsFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occured on the load of the global quests file.");
            logger.info(ChatColor.RED + "Please inform the developper.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Global quests file successfully loaded.");

        /* Easy quests */
        try {
            easyQuests.load(easyQuestsFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occured on the load of the easy quests file.");
            logger.info(ChatColor.RED + "Please inform the developper.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Easy quests file successfully loaded.");

        /* Medium quests */
        try {
            mediumQuests.load(mediumQuestsFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occured on the load of the medium quests file.");
            logger.info(ChatColor.RED + "Please inform the developper.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Medium quests file successfully loaded.");

        /* Hard quests */
        try {
            hardQuests.load(hardQuestsFile);
        } catch (InvalidConfigurationException | IOException e) {
            logger.info(ChatColor.RED + "An error occured on the load of the hard quests file.");
            logger.info(ChatColor.RED + "Please inform the developper.");
            e.printStackTrace();
        }
        logger.info(ChatColor.GREEN + "Hard quests file successfully loaded.");
    }

}
