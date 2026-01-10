package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.ProgressionLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.files.implementations.ProgressionFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoadProgressionYAML extends ProgressionLoader {

    private final ProgressionFile progressionFile;

    public LoadProgressionYAML(ProgressionFile progressionFile) {
        this.progressionFile = progressionFile;
    }

    public void loadPlayerQuests(String playerName, Map<String, PlayerQuests> activeQuests, boolean sendStatusMessage) {
        Debugger.write("Entering loadPlayerQuests (YAML) method for player " + playerName + ".");

        ODailyQuests.morePaperLib.scheduling().asyncScheduler().run(() -> {
            Debugger.write("Running async task to load progression of " + playerName + " from YAML file.");
            final FileConfiguration config = progressionFile.getConfig();
            final Player player = Bukkit.getPlayer(playerName);

            if (player == null) {
                handlePlayerDisconnected(playerName);
                return;
            }

            final String playerUuid = player.getUniqueId().toString();
            final ConfigurationSection playerSection = config.getConfigurationSection(playerUuid);

            if (playerSection == null) {
                handleNewPlayer(playerName, activeQuests);
                return;
            }

            loadExistingPlayerData(playerName, activeQuests, player, playerSection, sendStatusMessage);
        });
    }

    private void loadExistingPlayerData(
            String playerName,
            Map<String, PlayerQuests> activeQuests,
            Player player,
            ConfigurationSection playerSection,
            boolean sendStatusMessage
    ) {
        Debugger.write("Player " + playerName + " has data in progression file.");

        final long timestamp = playerSection.getLong(".timestamp");
        final int achievedQuests = playerSection.getInt(".achievedQuests");
        final int totalAchievedQuests = playerSection.getInt(".totalAchievedQuests");
        final int recentRerolls = playerSection.getInt(".recentRolls");

        final StoredPlayerProgression data = new StoredPlayerProgression(
                timestamp,
                achievedQuests,
                totalAchievedQuests,
                recentRerolls
        );

        final Map<String, Integer> totalAchievedQuestsByCategory = new HashMap<>();
        final ConfigurationSection statsSection = playerSection.getConfigurationSection("totalAchievedQuestsByCategory");
        if (statsSection != null) {
            for (String category : statsSection.getKeys(false)) {
                totalAchievedQuestsByCategory.put(category, statsSection.getInt(category));
            }
        }

        if (QuestLoaderUtils.checkTimestamp(data.timestamp())) {
            Debugger.write("Timestamp is too old for player " + playerName + ". " + NEW_QUESTS);
            QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuestsByCategory, data.totalAchievedQuests());
            return;
        }

        final LinkedHashMap<AbstractQuest, Progression> quests = loadPlayerQuestsFromConfig(playerName, playerSection);
        if (quests == null) {
            QuestLoaderUtils.loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuestsByCategory, data.totalAchievedQuests());
            return;
        }

        registerLoadedPlayerQuests(player, activeQuests, totalAchievedQuestsByCategory, quests, data, sendStatusMessage);
    }

    private LinkedHashMap<AbstractQuest, Progression> loadPlayerQuestsFromConfig(String playerName, ConfigurationSection playerSection) {
        final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();
        final ConfigurationSection questsSection = playerSection.getConfigurationSection(".quests");

        if (questsSection == null) {
            handleMissingQuests(playerName);
            return quests;
        }

        for (String key : questsSection.getKeys(false)) {
            final int questIndex = questsSection.getInt(key + ".index");
            final String categoryName = questsSection.getString(key + ".category");
            final int advancement = questsSection.getInt(key + ".progression");
            final int requiredAmount = questsSection.getInt(key + ".requiredAmount");
            final int selectedRequired = questsSection.getInt(key + ".selectedRequired", -1);

            // schema update check (1 to 2)
            if (requiredAmount == 0) {
                requiredAmountIsZero(playerName);
                return null;
            }

            final boolean isAchieved = questsSection.getBoolean(key + ".isAchieved");

            final AbstractQuest quest = QuestLoaderUtils.findQuest(playerName, categoryName, questIndex, Integer.parseInt(key));
            if (quest == null) {
                Debugger.write("Quest " + questIndex + " does not exist. " + NEW_QUESTS);
                return null;
            }

            if (!quest.isRandomRequired() && requiredAmount != Integer.parseInt(quest.getRequiredAmountRaw())) {
                requiredAmountNotEqual(playerName);
                return null;
            }

            // check if random quest have data
            if (isSelectedRequiredInvalid(quest, selectedRequired, playerName)) return null;

            final Progression progression = new Progression(requiredAmount, advancement, isAchieved);
            if (selectedRequired != -1) {
                progression.setSelectedRequiredIndex(selectedRequired);
            }

            quests.put(quest, progression);
        }

        return quests;
    }
}