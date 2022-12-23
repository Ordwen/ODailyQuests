package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.HashMap;

public class SaveProgressionYAML {

    /**
     * Save player progression in YAML file.
     *
     * @param playerName   player name.
     * @param playerQuests player quests.
     */
    public void saveProgression(String playerName, PlayerQuests playerQuests) {

        Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
            final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();

            /* init variables */
            long timestamp = playerQuests.getTimestamp();
            int achievedQuests = playerQuests.getAchievedQuests();
            int totalAchievedQuests = playerQuests.getTotalAchievedQuests();

            final HashMap<AbstractQuest, Progression> quests = playerQuests.getPlayerQuests();

            /* check if player has data */
            if (progressionFile.getString(playerName) != null) {

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

                PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s data saved.");
            }

            /* save the file */
            try {
                progressionFile.save(ProgressionFile.getProgressionFile());
            } catch (IOException e) {
                PluginLogger.info(ChatColor.RED + "An error happened on the save of the progression file.");
                PluginLogger.info(ChatColor.RED + "If the problem persists, contact the developer.");
                e.printStackTrace();
            }
        });
    }
}
