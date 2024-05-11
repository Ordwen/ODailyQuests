package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.api.events.AllCategoryQuestsCompletedEvent;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.api.events.AllQuestsCompletedEvent;
import com.ordwen.odailyquests.configuration.essentials.RerollNotAchieved;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.categories.Category;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Represents the quests of a player and its data.
 */
public class PlayerQuests {

    /* Timestamp of last quests renew */
    private Long timestamp;

    private int achievedQuests;
    private int totalAchievedQuests;
    private final LinkedHashMap<AbstractQuest, Progression> playerQuests;
    private final Map<String, Integer> achievedQuestsByCategory = new HashMap<>();
    private final Set<String> claimedRewards = new HashSet<>();

    public PlayerQuests(Long timestamp, LinkedHashMap<AbstractQuest, Progression> playerQuests) {
        this.timestamp = timestamp;
        this.playerQuests = playerQuests;
        this.achievedQuests = 0;
        this.totalAchievedQuests = 0;
    }

    /**
     * Get player timestamp.
     *
     * @return timestamp.
     */
    public Long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Increase number of achieved quests.
     *
     * @param player player who achieved a quest.
     */
    public void increaseAchievedQuests(QuestSystem questSystem, String category, Player player) {

        Debugger.addDebug("PlayerQuests: increaseAchievedQuests summoned by " + player.getName() + " for category " + category + ".");

        this.achievedQuests++;
        this.totalAchievedQuests++;

        if (this.achievedQuestsByCategory.containsKey(category)) {
            this.achievedQuestsByCategory.put(category, this.achievedQuestsByCategory.get(category) + 1);
        } else {
            this.achievedQuestsByCategory.put(category, 1);
        }

        /* check if the player have completed all quests from a category */
        if (questSystem.getQuestsMode() == 2) {
            for (Map.Entry<String, Integer> entry : this.achievedQuestsByCategory.entrySet()) {
                if (claimedRewards.contains(entry.getKey())) continue;
                if (entry.getValue() == QuestsAmount.getQuestsAmountByCategory(questSystem, entry.getKey())) {
                    final AllCategoryQuestsCompletedEvent event = new AllCategoryQuestsCompletedEvent(player, entry.getKey(), questSystem);
                    ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
                    claimedRewards.add(entry.getKey());
                }
            }
        }

        /* check if the player have completed all quests */
        if (this.achievedQuests == questSystem.getQuestsAmount()) {
            Debugger.addDebug("PlayerQuests: AllQuestsCompletedEvent is called.");

            final AllQuestsCompletedEvent event = new AllQuestsCompletedEvent(player, questSystem);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
        }
    }

    public void decreaseAchievedQuests() {
        this.achievedQuests--;
        this.totalAchievedQuests--;
    }

    /**
     * Set number of achieved quests.
     *
     * @param i number of achieved quests to set.
     */
    public void setAchievedQuests(int i) {
        this.achievedQuests = i;
    }

    /**
     * Set total number of achieved quests.
     *
     * @param i total number of achieved quests to set.
     */
    public void setTotalAchievedQuests(int i) {
        this.totalAchievedQuests = i;
    }

    /**
     * Add number of achieved quests.
     *
     * @param i number of achieved quests to add.
     */
    public void addTotalAchievedQuests(int i) {
        this.totalAchievedQuests += i;
    }

    /**
     * Get number of achieved quests.
     */
    public int getAchievedQuests() {
        return this.achievedQuests;
    }

    /**
     * Get total number of achieved quests.
     */
    public int getTotalAchievedQuests() {
        return this.totalAchievedQuests;
    }

    /**
     * Get player quests.
     *
     * @return a LinkedHashMap of quests and their progression.
     */
    public LinkedHashMap<AbstractQuest, Progression> getPlayerQuests() {
        return this.playerQuests;
    }

    /**
     * Set player timestamp.
     *
     * @param timestamp timestamp to set.
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Reroll a quest in the player quests.
     *
     * @param index index of the quest to reroll.
     */
    public boolean rerollQuest(QuestSystem questSystem, int index, Player player) {

        final List<AbstractQuest> oldQuests = new ArrayList<>(this.playerQuests.keySet());
        final AbstractQuest questToRemove = oldQuests.get(index);
        final Progression progressionToRemove = this.playerQuests.get(questToRemove);

        if (progressionToRemove.isAchieved() && RerollNotAchieved.isRerollIfNotAchieved()) {
            final String msg = QuestsMessages.CANNOT_REROLL_IF_ACHIEVED.toString();
            if (msg != null) player.sendMessage(msg);
            return false;
        }

        final Category category = CategoriesLoader.getCategoryByName(questSystem, questToRemove.getCategoryName());
        if (category == null) {
            PluginLogger.error("An error occurred while rerolling a quest. The category is null.");
            PluginLogger.error("If the problem persists, please contact the developer.");
            return false;
        }

        final Set<AbstractQuest> oldQuestsSet = this.playerQuests.keySet();
        oldQuestsSet.remove(questToRemove);

        final AbstractQuest newQuest = QuestsManager.getRandomQuestForPlayer(oldQuestsSet, category);

        final LinkedHashMap<AbstractQuest, Progression> newPlayerQuests = new LinkedHashMap<>();
        for (AbstractQuest quest : oldQuests) {
            if (quest.equals(questToRemove)) {
                final Progression progression = new Progression(0, false);
                newPlayerQuests.put(newQuest, progression);
            } else {
                final Progression progression = this.playerQuests.get(quest);
                newPlayerQuests.put(quest, progression);
            }
        }

        this.playerQuests.clear();
        this.playerQuests.putAll(newPlayerQuests);

        if (progressionToRemove.isAchieved()) {
            this.decreaseAchievedQuests();
        }

        return true;
    }
}
