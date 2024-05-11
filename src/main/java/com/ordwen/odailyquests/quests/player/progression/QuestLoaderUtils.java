package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QuestLoaderUtils {

    /**
     * Check if it is time to redraw quests for a player.
     *
     * @param timestamp player timestamp.
     * @return true if it's time to redraw quests.
     */
    public static boolean checkTimestamp(QuestSystem questSystem, Long timestamp) {

        /* check if last quests renewed day before */
        if (questSystem.getTimeStampMode() == 1) {

            final Calendar oldCal = Calendar.getInstance();
            final Calendar currentCal = Calendar.getInstance();
            oldCal.setTimeInMillis(timestamp);

            switch (questSystem.getTemporalityMode()) {
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
        else if (questSystem.getTimeStampMode() == 2) {
            switch (questSystem.getTemporalityMode()) {
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
                        PluginLogger.error(ChatColor.RED + "Impossible to check player quests timestamp. The " + questSystem.getSystemName() + " selected mode is incorrect");
            }
        } else
            PluginLogger.error(ChatColor.RED + "Impossible to check player quests timestamp. The " + questSystem.getSystemName() + " selected mode is incorrect");
        return false;
    }

    /**
     * Load quests for a player with no data.
     *
     * @param playerName   player name.
     * @param activeQuests all active quests.
     */
    public static void loadNewPlayerQuests(QuestSystem questSystem, String playerName, Map<String, PlayerQuests> activeQuests, int totalAchievedQuests) {

        activeQuests.remove(playerName);

        LinkedHashMap<AbstractQuest, Progression> quests = QuestsManager.selectRandomQuests(questSystem);
        PlayerQuests playerQuests;

        if (questSystem.getTimeStampMode() == 1) {
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

        final String msg = questSystem.getQUESTS_RENEWED_MESSAGE().getMessage(player);
        if (msg != null) player.sendMessage(msg);

        if (Bukkit.getPlayer(playerName) != null) {
            activeQuests.put(playerName, playerQuests);
            PluginLogger.fine(playerName + " inserted into the array.");
            PluginLogger.info(playerName + "'s quests have been renewed.");
        } else {
            PluginLogger.warn("It looks like " + playerName + " has disconnected before his quests were loaded.");
            return;
        }

        Debugger.addDebug("Quests of player " + playerName + " have been renewed.");
    }

    /**
     * Check if it's time to renew quests. If so, renew them.
     *
     * @param player       player.
     * @param activeQuests all active quests.
     * @return true if it's time to renew quests.
     */
    public static boolean isTimeToRenew(Player player, Map<String, PlayerQuests> activeQuests, QuestSystem questSystem) {
        if (questSystem.getTimeStampMode() == 1) return false;
        final PlayerQuests playerQuests = activeQuests.get(player.getName());

        if (checkTimestamp(questSystem, playerQuests.getTimestamp())) {
            loadNewPlayerQuests(questSystem, player.getName(), activeQuests, playerQuests.getTotalAchievedQuests());
            return true;
        }

        return false;
    }

    /**
     * Find quest with index in arrays.
     *
     * @param playerName player name.
     * @param questId id of quest in array.
     * @param id         number of player quest.
     * @return quest of index.
     */
    public static AbstractQuest findQuest(QuestSystem questSystem, String playerName, int questId, int id) {
        AbstractQuest quest = null;

        if (questSystem.getQuestsMode() == 1) {
            quest = getQuestAtIndex(questSystem.getGlobalCategory(), questId, playerName);
        } else if (questSystem.getQuestsMode() == 2) {

            final int questsAmount = questSystem.getQuestsAmount();

            if (id <= (questsAmount - questSystem.getMediumQuestsAmount() - questSystem.getHardQuestsAmount())) {
                quest = getQuestAtIndex(questSystem.getEasyCategory(), questId, playerName);
            } else if (id <= (questsAmount - questSystem.getHardQuestsAmount())) {
                quest = getQuestAtIndex(questSystem.getMediumCategory(), questId, playerName);
            } else {
                quest = getQuestAtIndex(questSystem.getHardCategory(), questId, playerName);
            }

        } else
            PluginLogger.error("Impossible to load player quests. The selected mode is incorrect.");

        if (quest == null) {
            PluginLogger.error("An error occurred while loading " + playerName + "'s quests.");
            PluginLogger.error("Quest number " + id + " of player is null.");
            PluginLogger.error("Try to do the following command to reset the player's progress :");
            PluginLogger.error("/questsadmin reset " + playerName);
            PluginLogger.error("If the problem persists, contact the developer.");
        }

        return quest;
    }

    /**
     * Try to get quest from index.
     *
     * @param questsArray the array where find the quest.
     * @param questId       the supposed id of the quest in the array.
     * @param playerName  the name of the player for whom the quest is intended.
     * @return the quest.
     */
    public static AbstractQuest getQuestAtIndex(ArrayList<AbstractQuest> questsArray, int questId, String playerName) {
        AbstractQuest quest;
        try {
            quest = getQuestWithId(questsArray, questId);
        } catch (Exception e) {

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

    public static AbstractQuest getQuestWithId(ArrayList<AbstractQuest> questsArray, int questId) {
        for (AbstractQuest quest : questsArray) {
            if (quest.getQuestId() == questId) {
                return quest;
            }
        }
        return null;
    }
}
