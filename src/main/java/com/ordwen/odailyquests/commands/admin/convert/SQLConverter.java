package com.ordwen.odailyquests.commands.admin.convert;

import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;

public abstract class SQLConverter {

    /**
     * Convert the progression file to the SQL database.
     *
     * @param progressionFile the progression file.
     * @param sqlManager      the SQL manager.
     */
    protected void convertData(FileConfiguration progressionFile, SQLManager sqlManager) {
        long timestamp;
        int achievedQuests;
        int totalAchievedQuests;
        PlayerQuests playerQuests;

        final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

        for (String playerUuid : progressionFile.getKeys(false)) {
            final ConfigurationSection playerSection = progressionFile.getConfigurationSection(playerUuid);
            if (playerSection == null) {
                error("SQLConverter, 31 - The player section is null.");
                return;
            }

            timestamp = playerSection.getLong(".timestamp");
            achievedQuests = playerSection.getInt(".achievedQuests");
            totalAchievedQuests = playerSection.getInt(".totalAchievedQuests");

            final ConfigurationSection questsSection = playerSection.getConfigurationSection(".quests");
            if (questsSection == null) {
                error("SQLConverter, 43 - The quests section is null.");
                return;
            }

            for (String string : questsSection.getKeys(false)) {
                final ConfigurationSection progressionSection = questsSection.getConfigurationSection(string);
                if (progressionSection == null) {
                    error("SQLConverter, 50 - The progression section is null.");
                    return;
                }

                int questIndex = progressionSection.getInt(".index");
                int advancement = progressionSection.getInt(".progression");
                int requiredAmount = progressionSection.getInt(".requiredAmount");
                boolean isAchieved = progressionSection.getBoolean(".isAchieved");

                final Progression progression = new Progression(requiredAmount, advancement, isAchieved);
                final AbstractQuest quest = QuestLoaderUtils.findQuest(playerUuid, questIndex, Integer.parseInt(string));
                if (quest == null) {
                    error("SQLConverter, 62 - The quest is null.");
                    return;
                }

                quests.put(quest, progression);
            }

            playerQuests = new PlayerQuests(timestamp, quests);
            playerQuests.setAchievedQuests(achievedQuests);
            playerQuests.setTotalAchievedQuests(totalAchievedQuests);

            sqlManager.getSaveProgressionSQL().saveProgression(playerUuid, playerUuid, playerQuests, true);
        }
    }

    /**
     * Log an error message.
     *
     * @param message the error message.
     */
    private void error(String message) {
        PluginLogger.error("An error occurred while converting YAML to SQL.");
        PluginLogger.error("If the error persists, please report it to the developer.");
        PluginLogger.error(message);
    }
}
