package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.quests.player.progression.ProgressionLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoadProgressionYAML extends ProgressionLoader {

    public void loadPlayerQuests(String playerName, Map<String, PlayerQuests> activeQuests) {
        Debugger.write("Entering loadPlayerQuests (YAML) method for player " + playerName + ".");

        ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
            Debugger.write("Running async task to load progression of " + playerName + " from YAML file.");
            final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();
            final Player player = Bukkit.getPlayer(playerName);

            if (player == null) {
                handlePlayerDisconnected(playerName);
                return;
            }

            final String playerUuid = player.getUniqueId().toString();
            final ConfigurationSection playerSection = progressionFile.getConfigurationSection(playerUuid);

            if (playerSection == null) {
                handleNewPlayer(playerName, activeQuests);
                return;
            }

            loadExistingPlayerData(playerName, activeQuests, player, playerSection);
        });
    }

    private void loadExistingPlayerData(String playerName, Map<String, PlayerQuests> activeQuests, Player player, ConfigurationSection playerSection) {
        Debugger.write("Player " + playerName + " has data in progression file.");

        final long timestamp = playerSection.getLong(".timestamp");
        final int achievedQuests = playerSection.getInt(".achievedQuests");
        final int totalAchievedQuests = playerSection.getInt(".totalAchievedQuests");

        final Map<String, Integer> totalAchievedQuestsByCategory = new HashMap<>();
        final ConfigurationSection statsSection = playerSection.getConfigurationSection("totalAchievedQuestsByCategory");
        if (statsSection != null) {
            for (String category : statsSection.getKeys(false)) {
                totalAchievedQuestsByCategory.put(category, statsSection.getInt(category));
            }
        }

        if (QuestLoaderUtils.checkTimestamp(timestamp)) {
            Debugger.write("Timestamp is too old for player " + playerName + ". Loading new quests.");
            QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuestsByCategory, totalAchievedQuests);
            return;
        }

        final LinkedHashMap<AbstractQuest, Progression> quests = loadPlayerQuestsFromConfig(playerName, playerSection);
        if (quests == null) {
            QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuestsByCategory, totalAchievedQuests);
            return;
        }

        final PlayerQuests playerQuests = new PlayerQuests(timestamp, quests);
        playerQuests.setAchievedQuests(achievedQuests);
        playerQuests.setTotalAchievedQuests(totalAchievedQuests);
        playerQuests.setTotalAchievedQuestsByCategory(totalAchievedQuestsByCategory);

        activeQuests.put(playerName, playerQuests);
        PluginLogger.info(playerName + "'s quests have been loaded.");

        sendQuestStatusMessage(player, achievedQuests, playerQuests);
    }

    private LinkedHashMap<AbstractQuest, Progression> loadPlayerQuestsFromConfig(String playerName, ConfigurationSection playerSection) {
        final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();
        final ConfigurationSection questsSection = playerSection.getConfigurationSection(".quests");

        if (questsSection == null) {
            handleMissingQuests(playerName);
            return quests;
        }

        int i = 1;
        for (String key : questsSection.getKeys(false)) {
            if (i > QuestsPerCategory.getTotalQuestsAmount()) {
                logExcessQuests(playerName);
                break;
            }

            int questIndex = questsSection.getInt(key + ".index");
            int advancement = questsSection.getInt(key + ".progression");
            int requiredAmount = questsSection.getInt(key + ".requiredAmount");
            int selectedRequired = questsSection.getInt(key + ".selectedRequired", -1);

            // schema update check (1 to 2)
            if (requiredAmount == 0) {
                Debugger.write("Required amount is 0 for player " + playerName + ". New quests will be drawn.");
                return null;
            }

            boolean isAchieved = questsSection.getBoolean(key + ".isAchieved");

            final AbstractQuest quest = QuestLoaderUtils.findQuest(playerName, questIndex, Integer.parseInt(key));
            if (quest == null) {
                Debugger.write("Quest " + questIndex + " does not exist. New quests will be drawn.");
                return null;
            }

            // check if random quest have data
            if (isSelectedRequiredInvalid(quest, selectedRequired, playerName)) return null;

            final Progression progression = new Progression(requiredAmount, advancement, isAchieved);
            if (selectedRequired != -1) {
                progression.setSelectedRequiredIndex(selectedRequired);
            }

            quests.put(quest, progression);
            i++;
        }

        return quests;
    }
}
