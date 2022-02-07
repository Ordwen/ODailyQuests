package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.util.HashMap;
import java.util.logging.Logger;

public class LoadProgression {

    /* Logger for stacktrace */
    private static Logger logger = PluginLogger.getLogger("ODailyQuests");

    /**
     * Getting instance of classes.
     */
    private static ProgressionFile progressionFile;

    /**
     * Class instance constructor.
     *
     * @param progressionFile progression file class.
     */
    public LoadProgression(ProgressionFile progressionFile) {
        LoadProgression.progressionFile = progressionFile;
    }

    /**
     * Load or renewed quotidian quests of player.
     * @param playerName player.
     * @param activeQuests list of active players.
     */
    public static void loadPlayerQuests(String playerName, HashMap<String, PlayerQuests> activeQuests, int configMode) {

        /* init variables */
        Long timestamp;
        PlayerQuests playerQuests;
        Quest quest = null;
        Progression progression;
        int questIndex;
        int advancement;
        boolean isAchieved;
        HashMap<Quest, Progression> quests = new HashMap<>();

        /* check if player has data */
        if (progressionFile.getProgressionFile().getString(playerName) != null) {

            timestamp = progressionFile.getProgressionFile().getConfigurationSection(playerName).getLong(".timestamp");

            /* DEBUG */
            //logger.info("Current timestamp : " + System.currentTimeMillis());
            //logger.info(playerName + " timestamp : " + timestamp);
            //logger.info("Difference : " + (System.currentTimeMillis() - timestamp));


            /* check if last quests renewed is older than 24 hours */
            if (System.currentTimeMillis() - timestamp >= 86400000) {
                activeQuests.remove(playerName);
                QuestsManager.selectRandomQuests(quests);
                playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
                activeQuests.put(playerName, playerQuests);
                Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_RENEWED.getMessage());

                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been renewed.");
            }
            /* load non-achieved quests */
            else {
                for (String string : progressionFile.getProgressionFile().getConfigurationSection(playerName + ".quests").getKeys(false)) {
                    questIndex = progressionFile.getProgressionFile().getConfigurationSection(playerName + ".quests." + string).getInt(".index");
                    advancement = progressionFile.getProgressionFile().getConfigurationSection(playerName + ".quests." + string).getInt(".progression");
                    isAchieved = progressionFile.getProgressionFile().getConfigurationSection(playerName + ".quests." + string).getBoolean(".isAchieved");

                    progression = new Progression(advancement, isAchieved);

                    if (configMode == 1) {
                        quest = LoadQuests.getGlobalQuests().get(questIndex);
                    } else {
                        switch(Integer.parseInt(string)) {
                            case 1:
                                quest = LoadQuests.getEasyQuests().get(questIndex);
                                break;
                            case 2:
                                quest = LoadQuests.getMediumQuests().get(questIndex);
                                break;
                            case 3:
                                quest = LoadQuests.getHardQuests().get(questIndex);
                                break;
                        }
                    }

                    if (quest == null) {
                        logger.info(ChatColor.RED + "An error occurred while loading " + ChatColor.GOLD + playerName + ChatColor.RED + "'s quests.");
                        logger.info(ChatColor.RED + "Quest number " + string + " of player is null.");
                        logger.info(ChatColor.RED + "Try to do the following command to reset the player's progress :");
                        logger.info(ChatColor.GOLD + "/questsadmin reset " + playerName);
                        logger.info(ChatColor.RED + "If the problem persists, contact the developer.");
                    }

                    quests.put(quest, progression);
                }

                playerQuests = new PlayerQuests(timestamp, quests);
                activeQuests.put(playerName, playerQuests);

                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been loaded.");
                Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_IN_PROGRESS.getMessage());
            }
        } else {
            QuestsManager.selectRandomQuests(quests);
            playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
            activeQuests.put(playerName, playerQuests);

            logger.info(ChatColor.GREEN + playerName + ChatColor.YELLOW + " inserted into the array.");
            Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_RENEWED.getMessage());
        }
    }
}
