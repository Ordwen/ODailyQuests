package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Logs;
import com.ordwen.odailyquests.files.implementations.ProgressionFile;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.Map;

public class SaveProgressionYAML {

    private final ProgressionFile progressionFile;

    public SaveProgressionYAML(ProgressionFile progressionFile) {
        this.progressionFile = progressionFile;
    }

    public void saveProgression(String playerName, String playerUuid, PlayerQuests playerQuests, boolean isServerStopping) {
        if (isServerStopping) updateFile(playerName, playerUuid, playerQuests);
        else ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> updateFile(playerName, playerUuid, playerQuests));
    }

    private void updateFile(String playerName, String playerUuid, PlayerQuests playerQuests) {
        final FileConfiguration config = progressionFile.getConfig();

        long timestamp = playerQuests.getTimestamp();
        int achievedQuests = playerQuests.getAchievedQuests();
        int totalAchievedQuests = playerQuests.getTotalAchievedQuests();

        final Map<AbstractQuest, Progression> quests = playerQuests.getQuests();

        config.set(playerUuid + ".timestamp", timestamp);
        config.set(playerUuid + ".achievedQuests", achievedQuests);
        config.set(playerUuid + ".totalAchievedQuests", totalAchievedQuests);

        int index = 1;
        for (Map.Entry<AbstractQuest, Progression> entry : quests.entrySet()) {
            final AbstractQuest quest = entry.getKey();
            final Progression progression = entry.getValue();

            final ConfigurationSection questSection = config.createSection(playerUuid + ".quests." + index);
            questSection.set("index", quest.getQuestIndex());
            questSection.set("progression", progression.getAdvancement());
            questSection.set("requiredAmount", progression.getRequiredAmount());
            questSection.set("selectedRequired", progression.getSelectedRequiredIndex());
            questSection.set("isAchieved", progression.isAchieved());

            index++;
        }

        final ConfigurationSection statsSection = config.createSection(playerUuid + ".totalAchievedQuestsByCategory");
        for (Map.Entry<String, Integer> entry : playerQuests.getTotalAchievedQuestsByCategory().entrySet()) {
            final String category = entry.getKey();
            final int amount = entry.getValue();
            statsSection.set(category, amount);
        }

        if (Logs.isEnabled()) {
            PluginLogger.info(playerName + "'s data saved.");
        }

        try {
            config.save(progressionFile.getFile());
        } catch (IOException e) {
            PluginLogger.error("An error happened on the save of the progression file.");
            PluginLogger.error("If the problem persists, contact the developer.");
            PluginLogger.error(e.getMessage());
        }
    }
}