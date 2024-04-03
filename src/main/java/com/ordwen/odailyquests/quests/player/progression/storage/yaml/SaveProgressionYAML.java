package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Logs;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.LinkedHashMap;

public class SaveProgressionYAML {

    /**
     * Save player progression in YAML file.
     *
     * @param playerName   player name.
     * @param playerQuests player quests.
     */
    public void saveProgression(String playerName, PlayerQuests playerQuests, boolean isAsync) {

        if (isAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> updateFile(playerName, playerQuests));
        } else updateFile(playerName, playerQuests);
    }

    private void updateFile(String playerName, PlayerQuests playerQuests) {
        final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();

        long timestamp = playerQuests.getTimestamp();
        int achievedQuests = playerQuests.getAchievedQuests();
        int totalAchievedQuests = playerQuests.getTotalAchievedQuests();

        final LinkedHashMap<AbstractQuest, Progression> quests = playerQuests.getPlayerQuests();

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

        if (Logs.isEnabled()) PluginLogger.info(playerName + "'s data saved.");

        try {
            progressionFile.save(ProgressionFile.getProgressionFile());
        } catch (IOException e) {
            PluginLogger.error("An error happened on the save of the progression file.");
            PluginLogger.error("If the problem persists, contact the developer.");
            PluginLogger.error(e.getMessage());
        }
    }
}
