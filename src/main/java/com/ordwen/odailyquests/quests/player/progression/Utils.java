package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

public class Utils {

    /**
     * Check if it is time to redraw quests for a player.
     *
     * @param timestampConfigMode quests config mode.
     * @param timestamp           player timestamp.
     * @return true if it's time to redraw quests.
     */
    public static boolean checkTimestamp(int timestampConfigMode, int temporalityMode, long timestamp) {

        /* check if last quests renewed day before */
        if (timestampConfigMode == 1) {

            Calendar oldCal = Calendar.getInstance();
            Calendar currentCal = Calendar.getInstance();
            oldCal.setTimeInMillis(timestamp);

            switch (temporalityMode) {
                case 1 -> {
                    currentCal.setTimeInMillis(System.currentTimeMillis());
                    if (oldCal.get(Calendar.YEAR) != currentCal.get(Calendar.YEAR)) return true;
                    return oldCal.get(Calendar.DAY_OF_YEAR) < currentCal.get(Calendar.DAY_OF_YEAR);
                }
                case 2 -> {
                    currentCal.setTimeInMillis(System.currentTimeMillis());
                    long diffW = TimeUnit.DAYS.convert(currentCal.getTimeInMillis() - oldCal.getTimeInMillis(), TimeUnit.MILLISECONDS);
                    return diffW >= 7;
                }
                case 3 -> {
                    currentCal.setTimeInMillis(System.currentTimeMillis());
                    long diffM = TimeUnit.DAYS.convert(currentCal.getTimeInMillis() - oldCal.getTimeInMillis(), TimeUnit.MILLISECONDS);
                    return diffM >= 31;
                }
            }
        }

        /* check if last quests renewed is older than selected temporality */
        else if (timestampConfigMode == 2) {
            switch (temporalityMode) {
                case 1 -> {
                    return System.currentTimeMillis() - timestamp >= 86400000L;
                }
                case 2 -> {
                    return System.currentTimeMillis() - timestamp >= 604800000L;
                }
                case 3 -> {
                    return System.currentTimeMillis() - timestamp >= 2678400000L;
                }
                default ->
                        PluginLogger.error(ChatColor.RED + "Impossible to check player quests timestamp. The selected mode is incorrect.");
            }
        } else
            PluginLogger.error(ChatColor.RED + "Impossible to load player quests timestamp. The selected mode is incorrect.");
        return false;
    }

    /**
     * Load quests for a player with no data.
     *
     * @param playerName          player name.
     * @param activeQuests        all active quests.
     * @param timestampConfigMode timestamp mode.
     */
    public static void loadNewPlayerQuests(String playerName, HashMap<String, PlayerQuests> activeQuests, int timestampConfigMode, int totalAchievedQuests) {

        activeQuests.remove(playerName);

        LinkedHashMap<AbstractQuest, Progression> quests = QuestsManager.selectRandomQuests();
        PlayerQuests playerQuests;

        if (timestampConfigMode == 1) {
            playerQuests = new PlayerQuests(Calendar.getInstance().getTimeInMillis(), quests);
        } else {
            playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
        }

        playerQuests.setTotalAchievedQuests(totalAchievedQuests);

        final Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            PluginLogger.warn("It seems that " + playerName + " disconnected before the end of the quest renewal.");
            return;
        }

        final String msg = QuestsMessages.QUESTS_RENEWED.toString();
        if (msg != null) player.sendMessage(msg);

        activeQuests.put(playerName, playerQuests);

        PluginLogger.fine(playerName + " inserted into the array.");
        PluginLogger.info(playerName + "'s quests have been renewed.");

        Debugger.addDebug("Quests of player " + playerName + " have been renewed.");
    }

    /**
     * Find quest with index in arrays.
     *
     * @param playerName       player name.
     * @param questsConfigMode quests mode.
     * @param questIndex       index of quest in array.
     * @param id               number of player quest.
     * @return quest of index.
     */
    public static AbstractQuest findQuest(String playerName, int questsConfigMode, int questIndex, int id) {
        AbstractQuest quest = null;

        if (questsConfigMode == 1) {
            quest = getQuestAtIndex(LoadQuests.getGlobalQuests(), questIndex, playerName);
        } else if (questsConfigMode == 2) {

            final int questsAmount = QuestsAmount.getQuestsAmount();

            if (id <= (questsAmount - QuestsAmount.getMediumQuestsAmount() - QuestsAmount.getHardQuestsAmount())) {
                quest = getQuestAtIndex(LoadQuests.getEasyQuests(), questIndex, playerName);
            }

            else if (id <= (questsAmount - QuestsAmount.getHardQuestsAmount())) {
                quest = getQuestAtIndex(LoadQuests.getMediumQuests(), questIndex, playerName);
            }

            else {
                quest = getQuestAtIndex(LoadQuests.getHardQuests(), questIndex, playerName);
            }

        } else
            PluginLogger.error("Impossible to load player quests. The selected mode is incorrect.");

        if (quest == null) {
            PluginLogger.error("An error occurred while loading " + playerName + "'s quests.");
            PluginLogger.error( "Quest number " + id + " of player is null.");
            PluginLogger.error("Try to do the following command to reset the player's progress :");
            PluginLogger.error("/questsadmin reset " + playerName);
            PluginLogger.error("If the problem persists, contact the developer.");
        }

        return quest;
    }

    /**
     * Try to get quest from index.
     * @param questsArray the array where find the quest.
     * @param index the supposed index of the quest in the array.
     * @param playerName the name of the player for whom the quest is intended.
     * @return the quest.
     */
    public static AbstractQuest getQuestAtIndex(ArrayList<AbstractQuest> questsArray, int index, String playerName) {
        AbstractQuest quest;
        try {
            quest = questsArray.get(index);
        } catch (IndexOutOfBoundsException e) {

            quest = questsArray.get(0);

            PluginLogger.error("A quest of the player " + playerName + " could not be loaded.");
            PluginLogger.error("This happens when a previously loaded quest has been deleted from the file.");
            PluginLogger.error("To avoid this problem, you should reset player progressions when you delete quests from the files.");
            PluginLogger.error("The first quest in the file was loaded instead.");
            PluginLogger.error("");
            PluginLogger.error("To reset the player's progress, do /qadmin reset " + playerName);
        }
        return quest;
    }
}
