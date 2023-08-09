package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    public void loadPlayerQuests(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode, int temporalityMode) {

        Bukkit.getScheduler().runTaskAsynchronously(ODailyQuests.INSTANCE, () -> {
            final FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();

            /* init variables */
            long timestamp;
            int achievedQuests;
            int totalAchievedQuests;
            PlayerQuests playerQuests;

            final LinkedHashMap<AbstractQuest, Progression> quests = new LinkedHashMap<>();

            /* check if player has data */
            if (progressionFile.getString(playerName) != null) {

                timestamp = progressionFile.getConfigurationSection(playerName).getLong(".timestamp");
                achievedQuests = progressionFile.getConfigurationSection(playerName).getInt(".achievedQuests");
                totalAchievedQuests = progressionFile.getConfigurationSection(playerName).getInt(".totalAchievedQuests");

                /* renew quests */
                if (Utils.checkTimestamp(timestampConfigMode, temporalityMode, timestamp)) {
                    Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, totalAchievedQuests);
                }
                /* load non-achieved quests */
                else {
                    int i = 1;

                    for (String string : progressionFile.getConfigurationSection(playerName + ".quests").getKeys(false)) {
                        if (i <= QuestsAmount.getQuestsAmount()) {
                            int questIndex = progressionFile.getConfigurationSection(playerName + ".quests." + string).getInt(".index");
                            int advancement = progressionFile.getConfigurationSection(playerName + ".quests." + string).getInt(".progression");
                            boolean isAchieved = progressionFile.getConfigurationSection(playerName + ".quests." + string).getBoolean(".isAchieved");

                            Progression progression = new Progression(advancement, isAchieved);
                            AbstractQuest quest = Utils.findQuest(playerName, questsConfigMode, questIndex, Integer.parseInt(string));

                            quests.put(quest, progression);
                            i++;
                        }
                        else {
                            PluginLogger.warn("Player " + playerName + " has more quests than the configuration.");
                            PluginLogger.warn("Only the first " + QuestsAmount.getQuestsAmount() + " quests will be loaded.");
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
                        msg = QuestsMessages.ALL_QUESTS_ACHIEVED_CONNECT.getMessage(playerName);
                    } else {
                        msg = QuestsMessages.QUESTS_IN_PROGRESS.getMessage(playerName);
                    }
                    if (msg != null) Bukkit.getPlayer(playerName).sendMessage(msg);
                }
            } else {
                Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, 0);
            }
        });
    }
}
