package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.configuration.essentials.*;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.enums.QuestsPermissions;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.categories.Category;
import com.ordwen.odailyquests.quests.categories.CategoryGroup;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.RenewSchedule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.entity.Player;

import java.time.*;
import java.util.*;

public class QuestLoaderUtils {

    private QuestLoaderUtils() {
    }

    /**
     * Check if it is time to redraw quests for a player.
     *
     * @param timestamp player timestamp.
     * @return true if it's time to redraw quests.
     */
    public static boolean checkTimestamp(long timestamp) {
        final int mode = TimestampMode.getTimestampMode();
        final Duration renewInterval = RenewInterval.getRenewInterval();

        switch (mode) {
            case 1 -> {
                final RenewSchedule.Settings s = RenewSchedule.settings();
                if (!RenewSchedule.isValid(s)) {
                    PluginLogger.error(ChatColor.RED + "Renew schedule is invalid.");
                    return false;
                }

                final ZonedDateTime lastRenew = Instant.ofEpochMilli(timestamp).atZone(s.zone());
                final ZonedDateTime now = ZonedDateTime.now(s.zone());

                return RenewSchedule.shouldRenewSince(lastRenew, now, s);
            }

            case 2 -> {
                if (renewInterval != null) {
                    return System.currentTimeMillis() - timestamp >= renewInterval.toMillis();
                } else {
                    PluginLogger.error(ChatColor.RED + "Impossible to check player quests timestamp. Renew interval is incorrect.");
                }
            }

            default ->
                    PluginLogger.error(ChatColor.RED + "Impossible to load player quests timestamp. The selected mode is incorrect.");
        }

        return false;
    }

    /**
     * Check if it is time to redraw quests for a player for a specific category group.
     *
     * @param timestamp player timestamp for this group
     * @param group     the category group to check
     * @return true if it's time to redraw quests for this group
     */
    public static boolean checkTimestamp(long timestamp, CategoryGroup group) {
        final int mode = TimestampMode.getTimestampMode();

        switch (mode) {
            case 1 -> {
                final RenewSchedule.Settings s = group.toScheduleSettings(mode);
                if (!RenewSchedule.isValid(s)) {
                    PluginLogger.error(ChatColor.RED + "Renew schedule is invalid for group '" + group.getName() + "'.");
                    return false;
                }

                final ZonedDateTime lastRenew = Instant.ofEpochMilli(timestamp).atZone(s.zone());
                final ZonedDateTime now = ZonedDateTime.now(s.zone());

                return RenewSchedule.shouldRenewSince(lastRenew, now, s);
            }

            case 2 -> {
                final Duration renewInterval = group.getRenewInterval();
                if (renewInterval != null) {
                    return System.currentTimeMillis() - timestamp >= renewInterval.toMillis();
                } else {
                    PluginLogger.error(ChatColor.RED + "Impossible to check player quests timestamp for group '" + group.getName() + "'. Renew interval is incorrect.");
                }
            }

            default ->
                    PluginLogger.error(ChatColor.RED + "Impossible to load player quests timestamp. The selected mode is incorrect.");
        }

        return false;
    }

    /**
     * Load quests for a player with no data.
     *
     * @param playerName   player name.
     * @param activeQuests all active quests.
     */
    public static void loadNewPlayerQuests(String playerName, Map<String, PlayerQuests> activeQuests, Map<String, Integer> totalAchievedQuestsByCategory, int totalAchievedQuests) {
        Debugger.write("Entering loadNewPlayerQuests method for player " + playerName + ".");
        activeQuests.remove(playerName);

        final Player player = Bukkit.getPlayer(playerName);
        Debugger.write("Attempting to renew quests for player " + playerName + ".");
        if (player == null) {
            Debugger.write("Player " + playerName + " is null. Impossible to renew quests.");
            PluginLogger.warn("It seems that " + playerName + " disconnected before the end of the quest renewal.");
            return;
        }

        final Map<AbstractQuest, Progression> quests = QuestsManager.selectRandomQuests(player);
        final PlayerQuests playerQuests;

        if (TimestampMode.getTimestampMode() == 1) {
            playerQuests = new PlayerQuests(Calendar.getInstance().getTimeInMillis(), quests);
        } else {
            playerQuests = new PlayerQuests(System.currentTimeMillis(), quests);
        }

        playerQuests.setTotalAchievedQuests(totalAchievedQuests);
        playerQuests.setTotalAchievedQuestsByCategory(totalAchievedQuestsByCategory);
        playerQuests.setRecentRerolls(0);

        final String msg = QuestsMessages.QUESTS_RENEWED.getMessage(player);
        if (msg != null && player.hasPermission(QuestsPermissions.QUESTS_PROGRESS.get())) {
            player.sendMessage(msg);
        }

        activeQuests.put(playerName, playerQuests);
        if (Logs.isEnabled()) {
            PluginLogger.info(playerName + "'s quests have been renewed.");
        }

        Debugger.write("Quests of player " + playerName + " have been renewed.");
    }

    /**
     * Load quests for a player for a specific category group only.
     * This is used for group-specific renewal where each group has its own schedule.
     *
     * @param playerName                    player name
     * @param activeQuests                  all active quests
     * @param totalAchievedQuestsByCategory total achieved quests by category
     * @param totalAchievedQuests           total achieved quests overall
     * @param group                         the category group to renew
     */
    public static void loadNewPlayerQuestsForGroup(String playerName, Map<String, PlayerQuests> activeQuests,
                                                    Map<String, Integer> totalAchievedQuestsByCategory,
                                                    int totalAchievedQuests, CategoryGroup group) {
        Debugger.write("Entering loadNewPlayerQuestsForGroup method for player " + playerName + " and group " + group.getName() + ".");

        final Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            Debugger.write("Player " + playerName + " is null. Impossible to renew quests for group " + group.getName() + ".");
            PluginLogger.warn("It seems that " + playerName + " disconnected before the end of the quest renewal.");
            return;
        }

        PlayerQuests playerQuests = activeQuests.get(playerName);
        if (playerQuests == null) {
            Debugger.write("PlayerQuests for " + playerName + " is null. Creating new player quests.");
            // If no existing quests, load all quests normally
            loadNewPlayerQuests(playerName, activeQuests, totalAchievedQuestsByCategory, totalAchievedQuests);
            return;
        }

        // Get the existing quests and keep those not in this group
        final Map<AbstractQuest, Progression> existingQuests = playerQuests.getQuests();
        final LinkedHashMap<AbstractQuest, Progression> newQuests = new LinkedHashMap<>();

        // Keep quests from other groups
        for (Map.Entry<AbstractQuest, Progression> entry : existingQuests.entrySet()) {
            final AbstractQuest quest = entry.getKey();
            final String categoryName = quest.getCategoryName();

            if (!group.containsCategory(categoryName)) {
                // This quest is not in the group being renewed, keep it
                newQuests.put(quest, entry.getValue());
            }
        }

        // Select new random quests for categories in this group
        final Map<AbstractQuest, Progression> groupQuests = QuestsManager.selectRandomQuestsForGroup(player, group);
        newQuests.putAll(groupQuests);

        // Update the timestamp for this group
        final long newTimestamp = TimestampMode.getTimestampMode() == 1 ?
                Calendar.getInstance().getTimeInMillis() : System.currentTimeMillis();

        // Create new PlayerQuests with updated timestamps
        final Map<String, Long> updatedTimestamps = new HashMap<>(playerQuests.getTimestampsByGroup());
        updatedTimestamps.put(group.getName(), newTimestamp);

        final PlayerQuests newPlayerQuests = new PlayerQuests(updatedTimestamps, newQuests);
        newPlayerQuests.setTotalAchievedQuests(totalAchievedQuests);
        newPlayerQuests.setTotalAchievedQuestsByCategory(totalAchievedQuestsByCategory);

        // Copy rerolls from other groups, reset for this group
        for (Map.Entry<String, Integer> rerollEntry : playerQuests.getRecentRerollsByGroup().entrySet()) {
            if (!rerollEntry.getKey().equals(group.getName())) {
                newPlayerQuests.setRecentRerolls(rerollEntry.getKey(), rerollEntry.getValue());
            }
        }
        newPlayerQuests.setRecentRerolls(group.getName(), 0);

        final String msg = QuestsMessages.QUESTS_RENEWED.getMessage(player);
        if (msg != null && player.hasPermission(QuestsPermissions.QUESTS_PROGRESS.get())) {
            player.sendMessage(msg.replace("%group%", group.getName()));
        }

        activeQuests.put(playerName, newPlayerQuests);
        if (Logs.isEnabled()) {
            PluginLogger.info(playerName + "'s quests for group '" + group.getName() + "' have been renewed.");
        }

        Debugger.write("Quests of player " + playerName + " for group " + group.getName() + " have been renewed.");
    }

    /**
     * Check if it's time to renew quests. If so, renew them.
     *
     * @param player       player.
     * @param activeQuests all active quests.
     * @return true if it's time to renew quests.
     */
    public static boolean isTimeToRenew(Player player, Map<String, PlayerQuests> activeQuests) {
        if (TimestampMode.getTimestampMode() == 1) return false;
        final PlayerQuests playerQuests = activeQuests.get(player.getName());

        if (checkTimestamp(playerQuests.getTimestamp())) {
            loadNewPlayerQuests(player.getName(), activeQuests, playerQuests.getTotalAchievedQuestsByCategory(), playerQuests.getTotalAchievedQuests());
            return true;
        }

        return false;
    }

    /**
     * Find quest with index in arrays.
     *
     * @param playerName player name.
     * @param questIndex index of quest in array.
     * @param id         number of player quest.
     * @return quest of index.
     */
    public static AbstractQuest findQuest(String playerName, int questIndex, int id) {
        AbstractQuest quest = null;

        final Map<String, Category> categoryMap = CategoriesLoader.getAllCategories();
        int totalQuestsCount = 0;

        for (Map.Entry<String, Category> entry : categoryMap.entrySet()) {
            String categoryName = entry.getKey();
            Category category = entry.getValue();
            int categoryQuestsAmount = QuestsPerCategory.getAmountForCategory(categoryName);

            if (id <= totalQuestsCount + categoryQuestsAmount) {
                quest = getQuestAtIndex(category, questIndex, playerName);
                break;
            }

            totalQuestsCount += categoryQuestsAmount;
        }

        if (quest == null) {
            PluginLogger.warn("Quest ID " + id + " was not found. Player quests will be reset.");
            PluginLogger.warn("This can happen after a server reload or if the quest was deleted from the file.");
        }

        return quest;
    }

    public static AbstractQuest findQuest(String playerName, String categoryName, int questIndex, int id) {
        if (categoryName != null && !categoryName.isEmpty()) {
            final Category category = CategoriesLoader.getCategoryByName(categoryName);
            if (category == null) {
                PluginLogger.warn("Category '" + categoryName + "' referenced in player " + playerName + " data no longer exists. New quests will be drawn for the player.");
                return null;
            }
            return getQuestAtIndex(category, questIndex, playerName);
        }

        return findQuest(playerName, questIndex, id);
    }

    /**
     * Try to get quest from index.
     *
     * @param category   the array where find the quest.
     * @param index      the supposed index of the quest in the array.
     * @param playerName the name of the player for whom the quest is intended.
     * @return the quest.
     */
    public static AbstractQuest getQuestAtIndex(Category category, int index, String playerName) {
        AbstractQuest quest = null;
        try {
            quest = category.get(index);
        } catch (IndexOutOfBoundsException e) {
            if (!category.isEmpty()) playerQuestMissing(playerName);
            else noQuestsAvailable();
        }

        return quest;
    }

    private static void playerQuestMissing(String playerName) {
        PluginLogger.warn("A quest of the player " + playerName + " could not be loaded.");
        PluginLogger.warn("This happens when a previously loaded quest has been deleted from the file.");
        PluginLogger.warn("To avoid this problem, you should reset player progressions when you delete quests.");
        PluginLogger.warn("New quests will be drawn for the player.");
    }

    private static void noQuestsAvailable() {
        PluginLogger.error("There is no quest at all available!");
        PluginLogger.error("It can happen if IA/Oraxen/Nexo integration is enabled without the corresponding plugin.");
        PluginLogger.error("Please check your configuration. If the problem persists, contact the developer.");
    }
}
