package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.io.File;
import java.io.IOException;

public class QuestsFiles {

    /**
     * Getting instance of main class.
     */
    private final ODailyQuests oDailyQuests;

    /**
     * Main class instance constructor.
     *
     * @param oDailyQuests main class.
     */
    public QuestsFiles(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Init quests files.
     */
    public void loadQuestsFiles() {

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            questSystem.setGlobalQuestsFile(new File(oDailyQuests.getDataFolder(), questSystem.getQuestsFilePath() + "globalQuests.yml"));
            questSystem.setEasyQuestsFile(new File(oDailyQuests.getDataFolder(), questSystem.getQuestsFilePath() + "easyQuests.yml"));
            questSystem.setMediumQuestsFile(new File(oDailyQuests.getDataFolder(), questSystem.getQuestsFilePath() + "mediumQuests.yml"));
            questSystem.setHardQuestsFile(new File(oDailyQuests.getDataFolder(), questSystem.getQuestsFilePath() + "hardQuests.yml"));

            /* Global quests */
            if (!questSystem.getGlobalQuestsFile().exists()) {
                oDailyQuests.saveResource(questSystem.getQuestsFilePath() + "globalQuests.yml", false);
                PluginLogger.info(questSystem.getSystemName() + " global quests file created.");
            }

            /* Easy quests */
            if (!questSystem.getEasyQuestsFile().exists()) {
                oDailyQuests.saveResource(questSystem.getQuestsFilePath() + "easyQuests.yml", false);
                PluginLogger.info(questSystem.getSystemName() + " easy quests file created.");
            }

            /* Medium quests */
            if (!questSystem.getMediumQuestsFile().exists()) {
                oDailyQuests.saveResource(questSystem.getQuestsFilePath() + "mediumQuests.yml", false);
                PluginLogger.info(questSystem.getSystemName() + " medium quests file created.");
            }

            /* Hard quests */
            if (!questSystem.getHardQuestsFile().exists()) {
                oDailyQuests.saveResource(questSystem.getQuestsFilePath() + "hardQuests.yml", false);
                PluginLogger.info(questSystem.getSystemName() + " hard quests file created.");
            }

            questSystem.setGlobalQuestsConfig(new YamlConfiguration());
            questSystem.setEasyQuestsConfig(new YamlConfiguration());
            questSystem.setMediumQuestsConfig(new YamlConfiguration());
            questSystem.setHardQuestsConfig(new YamlConfiguration());

            /* Global quests */
            try {
                questSystem.getGlobalQuestsConfig().load(questSystem.getGlobalQuestsFile());
            } catch (InvalidConfigurationException | IOException e) {
                PluginLogger.error("An error occured on the load of the " + questSystem.getSystemName() + " global quests file.");
                PluginLogger.error("Please inform the developer.");
                PluginLogger.error(e.getMessage());
            }
            PluginLogger.fine(questSystem.getSystemName() + " global quests file successfully loaded.");

            /* Easy quests */
            try {
                questSystem.getEasyQuestsConfig().load(questSystem.getEasyQuestsFile());
            } catch (InvalidConfigurationException | IOException e) {
                PluginLogger.error("An error occured on the load of the " + questSystem.getSystemName() + " easy quests file.");
                PluginLogger.error("Please inform the developer.");
                PluginLogger.error(e.getMessage());
            }
            PluginLogger.fine(questSystem.getSystemName() + " easy quests file successfully loaded.");

            /* Medium quests */
            try {
                questSystem.getMediumQuestsConfig().load(questSystem.getMediumQuestsFile());
            } catch (InvalidConfigurationException | IOException e) {
                PluginLogger.error("An error occured on the load of the " + questSystem.getSystemName() + " medium quests file.");
                PluginLogger.error("Please inform the developer.");
                PluginLogger.error(e.getMessage());
            }
            PluginLogger.fine(questSystem.getSystemName() + " medium quests file successfully loaded.");

            /* Hard quests */
            try {
                questSystem.getHardQuestsConfig().load(questSystem.getHardQuestsFile());
            } catch (InvalidConfigurationException | IOException e) {
                PluginLogger.error("An error occured on the load of the " + questSystem.getSystemName() + " hard quests file.");
                PluginLogger.error("Please inform the developer.");
                PluginLogger.error(e.getMessage());
            }
            PluginLogger.fine(questSystem.getSystemName() + " hard quests file successfully loaded.");


        });
    }

}
