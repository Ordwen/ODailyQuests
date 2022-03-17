package com.ordwen.odailyquests.quests.player.progression.storage;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginLogger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    /* init variables */
    private static final Logger logger = PluginLogger.getLogger("O'DailyQuests");

    /**
     * Check if it is time to redraw quests for a player.
     * @param timestampConfigMode quests config mode.
     * @param timestamp player timestamp.
     * @return true if it's time to redraw quests.
     */
    public static boolean checkTimestamp(int timestampConfigMode, long timestamp) {

        /* check if last quests renewed day before */
        if (timestampConfigMode == 1) {
            Calendar oldCal = Calendar.getInstance();
            Calendar currentCal = Calendar.getInstance();
            oldCal.setTimeInMillis(timestamp);
            currentCal.setTimeInMillis(System.currentTimeMillis());
            return oldCal.get(Calendar.DATE) < currentCal.get(Calendar.DATE);
        }

        /* check if last quests renewed is older than 24 hours */
        else if (timestampConfigMode == 2) {
            return System.currentTimeMillis() - timestamp >= 86400000;
        }
        else logger.log(Level.SEVERE, ChatColor.RED + "Impossible to load player quests timestamp. The selected mode is incorrect.");

        return false;
    }

    /**
     * Load quests for a player with no data.
     * @param playerName player name.
     * @param activeQuests all active quests.
     * @param timestampConfigMode timestamp mode.
     */
    public static void loadNewPlayerQuests(String playerName, HashMap<String, PlayerQuests> activeQuests, int timestampConfigMode, HashMap<Quest, Progression> quests) {

        activeQuests.remove(playerName);
        QuestsManager.selectRandomQuests(quests);
        PlayerQuests playerQuests;

        if (timestampConfigMode == 1) {
            playerQuests = new PlayerQuests(Calendar.getInstance().getTimeInMillis(), quests);
        } else {
            playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
        }

        activeQuests.put(playerName, playerQuests);

        Bukkit.getPlayer(playerName).sendMessage(QuestsMessages.QUESTS_RENEWED.toString());

        logger.info(ChatColor.GREEN + playerName + ChatColor.YELLOW + " inserted into the array.");
        logger.info(ChatColor.GOLD + playerName + ChatColor.YELLOW + "'s quests have been renewed.");
    }

    /**
     * Find quest with index in arrays.
     *
     * @param playerName player name.
     * @param questsConfigMode quests mode.
     * @param questIndex index of quest in array.
     * @param id number of player quest.
     * @return quest of index.
     */
    public static Quest findQuest(String playerName, int questsConfigMode, int questIndex, int id) {
        Quest quest = null;

        if (questsConfigMode == 1) {
            quest = LoadQuests.getGlobalQuests().get(questIndex);
        } else if (questsConfigMode == 2) {
            switch(id) {
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
            logger.info(ChatColor.RED + "Quest number " + id + " of player is null.");
            logger.info(ChatColor.RED + "Try to do the following command to reset the player's progress :");
            logger.info(ChatColor.GOLD + "/questsadmin reset " + playerName);
            logger.info(ChatColor.RED + "If the problem persists, contact the developer.");
        }

        return quest;
    }
}
