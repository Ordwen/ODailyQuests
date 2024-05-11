package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class LoadProgressionYAML {

    /**
     * Load or renewed quotidian quests of player.
     *
     * @param playerName   player.
     * @param activeQuests list of active players.
     */
    public void loadPlayerQuests(QuestSystem questSystem, String playerName, HashMap<String, PlayerQuests> activeQuests) {

        Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
            final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();

            /* init variables */
            long timestamp;
            int achievedQuests;
            int totalAchievedQuests;
            PlayerQuests playerQuests;

            final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

            /* check if player has data */
            if (progressionFile.getString(questSystem.getConfigPath() + playerName) != null) {

                timestamp = progressionFile.getConfigurationSection(questSystem.getConfigPath() + playerName).getLong(".timestamp");
                achievedQuests = progressionFile.getConfigurationSection(questSystem.getConfigPath() + playerName).getInt(".achievedQuests");
                totalAchievedQuests = progressionFile.getConfigurationSection(questSystem.getConfigPath() + playerName).getInt(".totalAchievedQuests");

                /* renew quests */
                if (QuestLoaderUtils.checkTimestamp(questSystem, timestamp)) {
                    QuestLoaderUtils.loadNewPlayerQuests(questSystem, playerName, activeQuests, totalAchievedQuests);
                }
                /* load non-achieved quests */
                else {
                    int i = 1;

                    for (String string : progressionFile.getConfigurationSection(questSystem.getConfigPath() + playerName + ".quests").getKeys(false)) {
                        if (i <= questSystem.getQuestsAmount()) {
                            int questId = progressionFile.getConfigurationSection(questSystem.getConfigPath() + playerName + ".quests." + string).getInt(".id");
                            int advancement = progressionFile.getConfigurationSection(questSystem.getConfigPath() + playerName + ".quests." + string).getInt(".progression");
                            boolean isAchieved = progressionFile.getConfigurationSection(questSystem.getConfigPath() + playerName + ".quests." + string).getBoolean(".isAchieved");

                            Progression progression = new Progression(advancement, isAchieved);
                            AbstractQuest quest = QuestLoaderUtils.findQuest(questSystem, playerName, questId, Integer.parseInt(string));

                            quests.put(quest, progression);
                            i++;
                        }
                        else {
                            PluginLogger.warn("Player " + playerName + " has more quests than the configuration.");
                            PluginLogger.warn("Only the first " + questSystem.getQuestsAmount() + " quests will be loaded.");
                            PluginLogger.warn("After changing the number of quests, we recommend that you reset the progressions to avoid any problems.");
                            break;
                        }
                    }

                    playerQuests = new PlayerQuests(timestamp, quests);
                    playerQuests.setAchievedQuests(achievedQuests);
                    playerQuests.setTotalAchievedQuests(totalAchievedQuests);

                    if (Bukkit.getPlayer(playerName) != null) {
                        activeQuests.put(playerName, playerQuests);
                        PluginLogger.info(playerName + "'s quests have been loaded.");
                    } else {
                        PluginLogger.warn("It looks like " + playerName + " has disconnected before his quests were loaded.");
                        return;
                    }

                    final String msg;
                    if (achievedQuests == playerQuests.getPlayerQuests().size()) {
                        msg = questSystem.getALL_QUESTS_ACHIEVED().getMessage(playerName);
                    } else {
                        msg = questSystem.getQUESTS_IN_PROGRESS().getMessage(playerName);
                    }
                    if (msg != null) Bukkit.getPlayer(playerName).sendMessage(msg);
                }
            } else {
                QuestLoaderUtils.loadNewPlayerQuests(questSystem, playerName, activeQuests, 0);
            }
        });
    }
}
