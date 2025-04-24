package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Logs;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Map;

public class SaveProgressionYAML {

    public void saveProgression(String playerName, String playerUuid, PlayerQuests playerQuests, boolean isServerStopping) {

        if (isServerStopping) updateFile(playerName, playerUuid, playerQuests);
        else ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> updateFile(playerName, playerUuid, playerQuests));
    }

    private void updateFile(String playerName, String playerUuid, PlayerQuests playerQuests) {
        final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();

        long timestamp = playerQuests.getTimestamp();
        int achievedQuests = playerQuests.getAchievedQuests();
        int totalAchievedQuests = playerQuests.getTotalAchievedQuests();

        final Map<AbstractQuest, Progression> quests = playerQuests.getQuests();

        progressionFile.set(playerUuid + ".timestamp", timestamp);
        progressionFile.set(playerUuid + ".achievedQuests", achievedQuests);
        progressionFile.set(playerUuid + ".totalAchievedQuests", totalAchievedQuests);

        int index = 1;
        for (Map.Entry<AbstractQuest, Progression> entry : quests.entrySet()) {
            final AbstractQuest quest = entry.getKey();
            final Progression progression = entry.getValue();

            final ConfigurationSection questSection = progressionFile.createSection(playerUuid + ".quests." + index);
            questSection.set("index", quest.getQuestIndex());
            questSection.set("progression", progression.getAdvancement());
            questSection.set("requiredAmount", progression.getRequiredAmount());
            questSection.set("selectedRequired", progression.getSelectedRequiredIndex());
            questSection.set("isAchieved", progression.isAchieved());

            index++;
        }

        if (Logs.isEnabled()) {
            PluginLogger.info(playerName + "'s data saved.");
        }

        try {
            progressionFile.save(ProgressionFile.getProgressionFile());
        } catch (IOException e) {
            PluginLogger.error("An error happened on the save of the progression file.");
            PluginLogger.error("If the problem persists, contact the developer.");
            PluginLogger.error(e.getMessage());
        }
    }
}
