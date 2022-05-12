package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.HashMap;

public class SaveProgressionYAML {

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
     * Save player progression in YAML file.
     *
     * @param playerName player name.
     * @param activeQuests player quests.
     */
    public static void saveProgression(String playerName, HashMap<String, PlayerQuests> activeQuests) {

        /* init variables */
        PlayerQuests playerQuests = activeQuests.get(playerName);
        long timestamp = playerQuests.getTimestamp();
        int achievedQuests = playerQuests.getAchievedQuests();
        HashMap<Quest, Progression> quests = playerQuests.getPlayerQuests();

        /* check if player has data */
        if (progressionFile.getProgressionFileConfiguration().getString(playerName) != null) {
            PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " detected into file data.");
            progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName).set(".timestamp", timestamp);
            progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName).set(".achievedQuests", achievedQuests);

            int index = 1;
            for (Quest quest : quests.keySet()) {
                progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + index).set(".index", quest.getQuestIndex());
                progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + index).set(".progression", quests.get(quest).getProgression());
                progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + index).set(".isAchieved", quests.get(quest).isAchieved());
                index++;
            }
        } else {
            progressionFile.getProgressionFileConfiguration().set(playerName + ".timestamp", timestamp);
            progressionFile.getProgressionFileConfiguration().set(playerName + ".achievedQuests", achievedQuests);

            int index = 1;
            for (Quest quest : quests.keySet()) {
                progressionFile.getProgressionFileConfiguration().set(playerName + ".quests." + index + ".index", quest.getQuestIndex());
                progressionFile.getProgressionFileConfiguration().set(playerName + ".quests." + index + ".progression", quests.get(quest).getProgression());
                progressionFile.getProgressionFileConfiguration().set(playerName + ".quests." + index + ".isAchieved", quests.get(quest).isAchieved());
                index++;
            }
            PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " added to file data.");
        }

        /* save the file */
        try {
            progressionFile.getProgressionFileConfiguration().save(progressionFile.getProgressionFile());
            PluginLogger.info(ChatColor.GOLD + "File data successfully saved.");
        } catch (IOException e) {
            PluginLogger.info(ChatColor.RED + "An error happened on the save of the progression file.");
            PluginLogger.info(ChatColor.RED + "If the problem persists, contact the developer.");
            e.printStackTrace();
        }
    }
}
