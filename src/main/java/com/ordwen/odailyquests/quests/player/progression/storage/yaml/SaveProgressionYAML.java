package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class SaveProgressionYAML {

    /* Logger for stacktrace */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /**
     * Getting instance of classes.
     */
    private static ProgressionFile progressionFile;

    /**
     * Class instance constructor.
     *
     * @param progressionFile progression file class.
     */
    public SaveProgressionYAML(ProgressionFile progressionFile) {
        SaveProgressionYAML.progressionFile = progressionFile;
    }

    /**
     * @param playerName
     * @param activeQuests
     */
    public static void saveProgression(String playerName, HashMap<String, PlayerQuests> activeQuests) {

        /* init variables */
        PlayerQuests playerQuests = activeQuests.get(playerName);
        long timestamp = playerQuests.getTimestamp();
        HashMap<Quest, Progression> quests = playerQuests.getPlayerQuests();

        /* check if player has data */
        if (progressionFile.getProgressionFileConfiguration().getString(playerName) != null) {
            logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " detected into file data.");
            progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName).set(".timestamp", timestamp);

            int index = 1;
            for (Quest quest : quests.keySet()) {
                progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + index).set(".index", quest.getQuestIndex());
                progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + index).set(".progression", quests.get(quest).getProgression());
                progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + index).set(".isAchieved", quests.get(quest).isAchieved());
                index++;
            }
        } else {
            progressionFile.getProgressionFileConfiguration().set(playerName + ".timestamp", timestamp);

            int index = 1;
            for (Quest quest : quests.keySet()) {
                progressionFile.getProgressionFileConfiguration().set(playerName + ".quests." + index + ".index", quest.getQuestIndex());
                progressionFile.getProgressionFileConfiguration().set(playerName + ".quests." + index + ".progression", quests.get(quest).getProgression());
                progressionFile.getProgressionFileConfiguration().set(playerName + ".quests." + index + ".isAchieved", quests.get(quest).isAchieved());
                index++;
            }
            logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " added to file data.");
        }

        /* save the file */
        try {
            progressionFile.getProgressionFileConfiguration().save(progressionFile.getProgressionFile());
            logger.info(ChatColor.GOLD + "File data successfully saved.");
        } catch (IOException e) {
            logger.info(ChatColor.RED + "An error happened on the save of the progression file.");
            logger.info(ChatColor.RED + "If the problem persists, contact the developer.");
            e.printStackTrace();
        }
    }
}
