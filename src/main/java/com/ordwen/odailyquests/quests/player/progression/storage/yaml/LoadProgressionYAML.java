package com.ordwen.odailyquests.quests.player.progression.storage.yaml;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.util.HashMap;
import java.util.logging.Logger;

public class LoadProgressionYAML {

    /* Logger for stacktrace */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /**
     * Getting instance of classes.
     */
    private static ProgressionFile progressionFile;

    /**
     * Class instance constructor.
     *
     * @param progressionFile progression file class.
     */
    public LoadProgressionYAML(ProgressionFile progressionFile) {
        LoadProgressionYAML.progressionFile = progressionFile;
    }

    /**
     * Load or renewed quotidian quests of player.
     *
     * @param playerName   player.
     * @param activeQuests list of active players.
     */
    public static void loadPlayerQuests(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode, int temporalityMode) {

        /* init variables */
        long timestamp;
        int achievedQuests;
        PlayerQuests playerQuests;
        HashMap<Quest, Progression> quests = new HashMap<>();

        /* check if player has data */
        if (progressionFile.getProgressionFileConfiguration().getString(playerName) != null) {

            timestamp = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName).getLong(".timestamp");
            achievedQuests = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName).getInt(".achievedQuests");

            /* renew quests */
            if (Utils.checkTimestamp(timestampConfigMode, temporalityMode, timestamp)) {
                Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, quests);
            }
            /* load non-achieved quests */
            else {
                for (String string : progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests").getKeys(false)) {
                    int questIndex = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + string).getInt(".index");
                    int advancement = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + string).getInt(".progression");
                    boolean isAchieved = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + string).getBoolean(".isAchieved");

                    Progression progression = new Progression(advancement, isAchieved);
                    Quest quest = Utils.findQuest(playerName, questsConfigMode, questIndex, Integer.parseInt(string));

                    quests.put(quest, progression);
                }

                playerQuests = new PlayerQuests(timestamp, quests);
                playerQuests.setAchievedQuests(achievedQuests);

                activeQuests.put(playerName, playerQuests);

                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been loaded.");
                Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_IN_PROGRESS.toString());
            }
        } else {
            Utils.loadNewPlayerQuests(playerName, activeQuests, timestampConfigMode, quests);
        }
    }
}
