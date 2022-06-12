package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
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
    public static void loadPlayerQuests(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode, int temporalityMode) {

        FileConfiguration progressionFile = ProgressionFile.getProgressionFileConfiguration();

        /* init variables */
        long timestamp;
        int achievedQuests;
        PlayerQuests playerQuests;
        LinkedHashMap<Quest, Progression> quests = new LinkedHashMap<>();

        /* check if player has data */
        if (progressionFile.getString(playerName) != null) {

            timestamp = progressionFile.getConfigurationSection(playerName).getLong(".timestamp");
            achievedQuests = progressionFile.getConfigurationSection(playerName).getInt(".achievedQuests");

            /* renew quests */
            if (Utils.checkTimestamp(timestampConfigMode, temporalityMode, timestamp)) {
                Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, quests);
            }
            /* load non-achieved quests */
            else {
                for (String string : progressionFile.getConfigurationSection(playerName + ".quests").getKeys(false)) {
                    int questIndex = progressionFile.getConfigurationSection(playerName + ".quests." + string).getInt(".index");
                    int advancement = progressionFile.getConfigurationSection(playerName + ".quests." + string).getInt(".progression");
                    boolean isAchieved = progressionFile.getConfigurationSection(playerName + ".quests." + string).getBoolean(".isAchieved");

                    Progression progression = new Progression(advancement, isAchieved);
                    Quest quest = Utils.findQuest(playerName, questsConfigMode, questIndex, Integer.parseInt(string));

                    quests.put(quest, progression);
                }

                playerQuests = new PlayerQuests(timestamp, quests);
                playerQuests.setAchievedQuests(achievedQuests);

                activeQuests.put(playerName, playerQuests);

                PluginLogger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been loaded.");
                Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_IN_PROGRESS.toString());
            }
        } else {
            Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, quests);
        }
    }
}
