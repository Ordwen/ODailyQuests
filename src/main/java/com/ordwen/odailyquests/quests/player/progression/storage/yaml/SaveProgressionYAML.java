package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.player.progression.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.HashMap;

public class SaveProgressionYAML {

    /**
     * Save player progression in YAML file.
     *
     * @param playerName player name.
     * @param playerQuests player quests.
     */
    public void saveProgression(String playerName, PlayerQuests playerQuests) {

        FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();

        /* init variables */
        long timestamp = playerQuests.getTimestamp();
        int achievedQuests = playerQuests.getAchievedQuests();
        int totalAchievedQuests = playerQuests.getTotalAchievedQuests();
        HashMap<AbstractQuest, Progression> quests = playerQuests.getPlayerQuests();

        /* check if player has data */
        if (progressionFile.getString(playerName) != null) {
            PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " detected into file data.");
            progressionFile.getConfigurationSection(playerName).set(".timestamp", timestamp);
            progressionFile.getConfigurationSection(playerName).set(".achievedQuests", achievedQuests);
            progressionFile.getConfigurationSection(playerName).set(".totalAchievedQuests", totalAchievedQuests);

            int index = 1;
            for (AbstractQuest quest : quests.keySet()) {
                progressionFile.getConfigurationSection(playerName + ".quests." + index).set(".index", quest.getQuestIndex());
                progressionFile.getConfigurationSection(playerName + ".quests." + index).set(".progression", quests.get(quest).getProgression());
                progressionFile.getConfigurationSection(playerName + ".quests." + index).set(".isAchieved", quests.get(quest).isAchieved());
                index++;
            }
        } else {
            progressionFile.set(playerName + ".timestamp", timestamp);
            progressionFile.set(playerName + ".achievedQuests", achievedQuests);
            progressionFile.set(playerName + ".totalAchievedQuests", totalAchievedQuests);

            int index = 1;
            for (AbstractQuest quest : quests.keySet()) {
                progressionFile.set(playerName + ".quests." + index + ".index", quest.getQuestIndex());
                progressionFile.set(playerName + ".quests." + index + ".progression", quests.get(quest).getProgression());
                progressionFile.set(playerName + ".quests." + index + ".isAchieved", quests.get(quest).isAchieved());
                index++;
            }
            PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + " added to file data.");
        }

        /* save the file */
        try {
            progressionFile.save(ProgressionFile.getProgressionFile());
            PluginLogger.info(ChatColor.GOLD + "File data successfully saved.");
        } catch (IOException e) {
            PluginLogger.info(ChatColor.RED + "An error happened on the save of the progression file.");
            PluginLogger.info(ChatColor.RED + "If the problem persists, contact the developer.");
            e.printStackTrace();
        }
    }
}
