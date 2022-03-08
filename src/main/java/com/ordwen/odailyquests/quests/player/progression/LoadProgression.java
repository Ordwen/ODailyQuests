package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.files.ProgressionFile;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadProgression {

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
    public LoadProgression(ProgressionFile progressionFile) {
        LoadProgression.progressionFile = progressionFile;
    }

    /**
     * Load or renewed quotidian quests of player.
     * @param playerName player.
     * @param activeQuests list of active players.
     */
    public static void loadPlayerQuests(String playerName, HashMap<String, PlayerQuests> activeQuests, int questsConfigMode, int timestampConfigMode) {

        /* init variables */
        long timestamp;
        PlayerQuests playerQuests;
        Quest quest = null;
        Progression progression;
        int questIndex;
        int advancement;
        boolean isAchieved;
        HashMap<Quest, Progression> quests = new HashMap<>();

        /* check if player has data */
        if (progressionFile.getProgressionFileConfiguration().getString(playerName) != null) {

            timestamp = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName).getLong(".timestamp");
            boolean timeToRedraw = false;

            /* check if last quests renewed day before */
            if (timestampConfigMode == 1) {
                Calendar oldCal = Calendar.getInstance();
                Calendar currentCal = Calendar.getInstance();
                oldCal.setTimeInMillis(timestamp);
                currentCal.setTimeInMillis(System.currentTimeMillis());
                if (oldCal.get(Calendar.DATE) < currentCal.get(Calendar.DATE)) {
                    timeToRedraw = true;
                }
            }

            /* check if last quests renewed is older than 24 hours */
            else if (timestampConfigMode == 2) {
                if (System.currentTimeMillis() - timestamp >= 86400000) {
                    timeToRedraw = true;
                }
            }
            else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load player quests timestamp. The selected mode is incorrect.");

            /* renew quests */
            if (timeToRedraw) {
                activeQuests.remove(playerName);
                QuestsManager.selectRandomQuests(quests);
                if (timestampConfigMode == 1) {
                    playerQuests = new PlayerQuests(Calendar.getInstance().getTimeInMillis(), quests);
                } else {
                    playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
                }
                activeQuests.put(playerName, playerQuests);
                Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_RENEWED.toString());

                logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been renewed.");
            }
            /* load non-achieved quests */
            else {
                for (String string : progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests").getKeys(false)) {
                    questIndex = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + string).getInt(".index");
                    advancement = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + string).getInt(".progression");
                    isAchieved = progressionFile.getProgressionFileConfiguration().getConfigurationSection(playerName + ".quests." + string).getBoolean(".isAchieved");

                    progression = new Progression(advancement, isAchieved);

                    if (questsConfigMode == 1) {
                        quest = LoadQuests.getGlobalQuests().get(questIndex);
                    } else if (questsConfigMode == 2) {
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
                    } else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load player quests. The selected mode is incorrect.");

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
                Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_IN_PROGRESS.toString());
            }
        } else {
            QuestsManager.selectRandomQuests(quests);
            if (timestampConfigMode == 1) {
                playerQuests = new PlayerQuests(Calendar.getInstance().getTimeInMillis(), quests);
            } else {
                playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
            }
            activeQuests.put(playerName, playerQuests);

            logger.info(ChatColor.GREEN + playerName + ChatColor.YELLOW + " inserted into the array.");
            Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_RENEWED.toString());
        }
    }
}
