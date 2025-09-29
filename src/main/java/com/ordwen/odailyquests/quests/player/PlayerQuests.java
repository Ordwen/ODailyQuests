package com.ordwen.odailyquests.quests.player;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.AllCategoryQuestsCompletedEvent;
import com.ordwen.odailyquests.api.events.CategoryTotalRewardReachedEvent;
import com.ordwen.odailyquests.api.events.TotalRewardReachedEvent;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.api.events.AllQuestsCompletedEvent;
import com.ordwen.odailyquests.configuration.essentials.RerollNotAchieved;
import com.ordwen.odailyquests.configuration.functionalities.rewards.TotalRewards;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.categories.Category;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Represents the player's quests and their associated data.
 * <p>
 * This class keeps track of the quests a player has completed, including the total number of completed quests,
 * quests achieved in each category, and handles quest progression and rerolling of quests.
 */
public class PlayerQuests {

    public enum ReplaceResult {
        SUCCESS,
        INVALID_INDEX,
        ALREADY_PRESENT
    }

    /* timestamp of last quests renew */
    private final Long timestamp;

    private int achievedQuests;
    private int totalAchievedQuests;
    private final Map<AbstractQuest, Progression> quests;
    private final Map<String, Integer> achievedQuestsByCategory = new HashMap<>();
    private final Map<String, Integer> totalAchievedQuestsByCategory = new HashMap<>();

    /**
     * Constructs a new PlayerQuests object with the provided timestamp and a map of quests with their progress.
     *
     * @param timestamp the last time the player's quests were renewed.
     * @param quests    a map of quests and their respective progression.
     */
    public PlayerQuests(Long timestamp, Map<AbstractQuest, Progression> quests) {
        this.timestamp = timestamp;
        this.quests = quests;
        this.achievedQuests = 0;
        this.totalAchievedQuests = 0;

        setAchievedQuestsByCategory();
    }

    /**
     * Sets the number of achieved quests by category when the player logs in.
     * This method iterates over the player's quests and updates the number of quests completed for each category.
     */
    private void setAchievedQuestsByCategory() {
        for (Map.Entry<AbstractQuest, Progression> entry : this.quests.entrySet()) {
            if (entry.getValue().isAchieved()) {
                final String category = entry.getKey().getCategoryName();
                if (this.achievedQuestsByCategory.containsKey(category)) {
                    this.achievedQuestsByCategory.put(category, this.achievedQuestsByCategory.get(category) + 1);
                } else {
                    this.achievedQuestsByCategory.put(category, 1);
                }
            }
        }
    }

    /**
     * Gets the player's timestamp.
     *
     * @return the timestamp of the player's last quest renew.
     */
    public Long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Increases the number of achieved quests for a given category.
     * <p>
     * If all quests from the category are completed, the {@link AllCategoryQuestsCompletedEvent} is triggered.
     * If the player has completed all quests, the {@link AllQuestsCompletedEvent} is triggered.
     *
     * @param category the category of the quest completed.
     * @param player   the player who achieved the quest.
     */
    public void increaseCategoryAchievedQuests(String category, Player player) {

        Debugger.write("PlayerQuests: increaseAchievedQuests summoned by " + player.getName() + " for category " + category + ".");

        this.achievedQuests++;
        this.totalAchievedQuests++;

        if (this.achievedQuestsByCategory.containsKey(category)) {
            this.achievedQuestsByCategory.put(category, this.achievedQuestsByCategory.get(category) + 1);
        } else {
            this.achievedQuestsByCategory.put(category, 1);
        }

        if (this.totalAchievedQuestsByCategory.containsKey(category)) {
            this.totalAchievedQuestsByCategory.put(category, this.totalAchievedQuestsByCategory.get(category) + 1);
        } else {
            this.totalAchievedQuestsByCategory.put(category, 1);
        }

        Debugger.write("PlayerQuests: increaseAchievedQuests: " + player.getName() + " has completed " + this.achievedQuestsByCategory.get(category) + " quests in category " + category + ".");

        if (this.achievedQuestsByCategory.get(category) == QuestsPerCategory.getAmountForCategory(category)) {
            Debugger.write("PlayerQuests: AllCategoryQuestsCompletedEvent is called.");
            final AllCategoryQuestsCompletedEvent event = new AllCategoryQuestsCompletedEvent(player, category);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
        }

        /* check if the player have completed all quests */
        if (this.achievedQuests == QuestsPerCategory.getTotalQuestsAmount()) {
            Debugger.write("PlayerQuests: AllQuestsCompletedEvent is called.");

            final AllQuestsCompletedEvent event = new AllQuestsCompletedEvent(player);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
        }

        if (TotalRewards.isGlobalStep(this.totalAchievedQuests)) {
            Debugger.write("PlayerQuests: TotalRewardReachedEvent is called for " + player.getName() + " with total achieved quests: " + this.totalAchievedQuests + ".");
            final TotalRewardReachedEvent event = new TotalRewardReachedEvent(player, this.totalAchievedQuests);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
        }

        if (TotalRewards.isCategoryStep(category, this.totalAchievedQuestsByCategory.get(category))) {
            Debugger.write("PlayerQuests: CategoryTotalRewardReachedEvent is called for " + player.getName() + " in category " + category + " with total achieved quests: " + this.totalAchievedQuestsByCategory.get(category) + ".");
            final CategoryTotalRewardReachedEvent event = new CategoryTotalRewardReachedEvent(player, category, this.totalAchievedQuestsByCategory.get(category));
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Rerolls a quest for the player at the given index.
     * <p>
     * Workflow:
     * <ol>
     *   <li>Read the quest at {@code index} and its progression.</li>
     *   <li>Validate that a reroll is allowed according to configuration and current progression.</li>
     *   <li>Resolve the quest category and build a working set that excludes the quest being replaced.</li>
     *   <li>Pick a random replacement quest (not already assigned and permitted for the player).</li>
     *   <li>Rebuild the ordered map of quests, inserting a fresh progression for the new quest.</li>
     *   <li>If the removed quest was achieved, update category/global counters accordingly.</li>
     * </ol>
     *
     * <p><strong>Side effects:</strong> Mutates this instance's {@code quests} map,
     * potentially updates achievement counters, and may send feedback messages to the player.
     *
     * @param index  zero-based slot of the quest to reroll (must be within bounds of the current ordered keys)
     * @param player the player for whom the reroll is performed (used for permission checks and messaging)
     * @return {@code true} if the reroll succeeded; {@code false} otherwise (e.g., reroll not allowed,
     * no available quest, or category resolution error)
     * @throws IndexOutOfBoundsException if {@code index} is out of range for the current quest list
     */
    public boolean rerollQuest(int index, Player player) {
        // Snapshot ordered keys to address a specific slot consistently.
        final List<AbstractQuest> oldQuests = new ArrayList<>(this.quests.keySet());
        final AbstractQuest questToRemove = oldQuests.get(index);
        final Progression progressionToRemove = this.quests.get(questToRemove);

        // Guard: configuration may disallow rerolling already achieved quests.
        if (!isRerollAllowed(progressionToRemove, player)) {
            return false;
        }

        // Resolve category that must provide the replacement quest.
        final String categoryName = questToRemove.getCategoryName();
        final Category category = CategoriesLoader.getCategoryByName(categoryName);
        if (category == null) {
            logCategoryNullError();
            return false;
        }

        // Work on a copy to avoid mutating the live key set while filtering.
        final Set<AbstractQuest> currentWithoutRemoved = new HashSet<>(this.quests.keySet());
        currentWithoutRemoved.remove(questToRemove);

        // Pick a replacement quest not already assigned and allowed by permissions.
        final AbstractQuest newQuest = QuestsManager.getRandomQuestForPlayer(currentWithoutRemoved, category, player);
        if (newQuest == null) {
            notifyNoAvailableQuests(player, categoryName);
            return false;
        }

        // Rebuild the ordered map with the new quest (fresh progression) at the same position.
        final LinkedHashMap<AbstractQuest, Progression> newPlayerQuests =
                rebuildQuestsMap(oldQuests, questToRemove, newQuest);

        // Apply the new map atomically.
        this.quests.clear();
        this.quests.putAll(newPlayerQuests);

        // If the removed quest was previously achieved, adjust counters accordingly.
        updateAchievementsAfterRerollIfNeeded(progressionToRemove, categoryName, questToRemove);
        return true;
    }

    /**
     * Checks whether the current configuration allows rerolling the given progression.
     * If rerolling achieved quests is disallowed, a feedback message is sent to the player.
     *
     * @param progression progression of the quest being rerolled
     * @param player      player to notify if rerolling is disallowed
     * @return {@code true} if rerolling is allowed; {@code false} otherwise
     */
    private boolean isRerollAllowed(Progression progression, Player player) {
        if (progression.isAchieved() && RerollNotAchieved.isRerollIfNotAchieved()) {
            final String msg = QuestsMessages.CANNOT_REROLL_IF_ACHIEVED.toString();
            if (msg != null) player.sendMessage(msg);
            return false;
        }
        return true;
    }

    /**
     * Logs a consistent error when the quest's category cannot be resolved.
     * This typically indicates a misconfiguration or a category that was removed.
     */
    private void logCategoryNullError() {
        PluginLogger.error("An error occurred while rerolling a quest. The category is null.");
        PluginLogger.error("If the problem persists, please contact the developer.");
    }

    /**
     * Notifies the player that no eligible replacement quest could be found in the given category.
     *
     * @param player       player to notify
     * @param categoryName category where we attempted to pick a new quest
     */
    private void notifyNoAvailableQuests(Player player, String categoryName) {
        final String msg = QuestsMessages.NO_AVAILABLE_QUESTS_IN_CATEGORY.toString();
        if (msg != null) player.sendMessage(msg.replace("%category%", categoryName));
    }

    /**
     * Rebuilds an ordered {@link LinkedHashMap} of quests where the specified quest is replaced
     * by {@code newQuest} with a fresh {@link Progression}. All other quests retain their existing
     * {@link Progression} instances.
     *
     * @param oldQuests     snapshot of the previous quest order
     * @param questToRemove quest to be replaced
     * @param newQuest      quest to insert at the same position (with fresh progression)
     * @return a new ordered map representing the updated assignment
     */
    private LinkedHashMap<AbstractQuest, Progression> rebuildQuestsMap(List<AbstractQuest> oldQuests, AbstractQuest questToRemove, AbstractQuest newQuest) {
        final LinkedHashMap<AbstractQuest, Progression> map = new LinkedHashMap<>();
        for (AbstractQuest quest : oldQuests) {
            if (quest.equals(questToRemove)) {
                map.put(newQuest, QuestsManager.createFreshProgression(newQuest));
            } else {
                map.put(quest, this.quests.get(quest));
            }
        }
        return map;
    }

    /**
     * Adjusts achievement counters and category totals if the removed quest was achieved.
     * <p>
     * If the category had already reached its maximum completed count, no change is required.
     * Otherwise, the category's achieved count is decremented and a debug message is emitted.
     *
     * @param removedProgression progression of the quest that was rerolled away
     * @param categoryName       category name for counter adjustments
     * @param questToRemove      quest that was removed (for category lookup)
     */
    private void updateAchievementsAfterRerollIfNeeded(Progression removedProgression, String categoryName, AbstractQuest questToRemove) {
        if (!removedProgression.isAchieved()) return;

        this.decreaseAchievedQuests();

        final int achievedByCategory = this.achievedQuestsByCategory.get(categoryName);
        final int totalForCategory = QuestsPerCategory.getAmountForCategory(categoryName);

        // If the category was fully completed, there's nothing to decrement.
        if (achievedByCategory >= totalForCategory) {
            Debugger.write("All quests from category " + categoryName + " have been completed. Nothing to do.");
            return;
        }

        this.achievedQuestsByCategory.put(questToRemove.getCategoryName(), achievedByCategory - 1);
        Debugger.write("Quest removed from category " + categoryName + ". " +
                "Quests completed: " + (achievedByCategory - 1) + "/" + totalForCategory + ".");
    }

    /**
     * Replaces the quest stored at the provided index with a new quest instance.
     * <p>
     * The newly assigned quest starts with a fresh {@link Progression}, mirroring the behaviour of
     * the daily quest draw. If the replaced quest was already achieved, the player's counters are
     * adjusted accordingly.
     *
     * @param index    zero-based index of the quest to replace
     * @param newQuest the quest that should replace the current one
     * @return the result of the replacement attempt
     */
    public ReplaceResult setQuestAtIndex(int index, AbstractQuest newQuest) {
        final List<AbstractQuest> orderedQuests = new ArrayList<>(this.quests.keySet());

        if (quests.containsKey(newQuest)) {
            return ReplaceResult.ALREADY_PRESENT;
        }

        if (index < 0 || index >= orderedQuests.size()) {
            return ReplaceResult.INVALID_INDEX;
        }

        final AbstractQuest questToReplace = orderedQuests.get(index);
        final Progression oldProgression = this.quests.get(questToReplace);

        final LinkedHashMap<AbstractQuest, Progression> updatedQuests = new LinkedHashMap<>();
        for (int i = 0; i < orderedQuests.size(); i++) {
            final AbstractQuest quest = orderedQuests.get(i);
            if (i == index) {
                updatedQuests.put(newQuest, QuestsManager.createFreshProgression(newQuest));
            } else {
                updatedQuests.put(quest, this.quests.get(quest));
            }
        }

        this.quests.clear();
        this.quests.putAll(updatedQuests);

        if (oldProgression != null && oldProgression.isAchieved()) {
            this.decreaseAchievedQuests();

            final String oldCategory = questToReplace.getCategoryName();
            final int achievedByCategory = this.achievedQuestsByCategory.getOrDefault(oldCategory, 0);
            if (achievedByCategory > 0) {
                this.achievedQuestsByCategory.put(oldCategory, achievedByCategory - 1);
            }
        }

        return ReplaceResult.SUCCESS;
    }

    /**
     * Decreases the number of achieved quests by 1.
     */
    public void decreaseAchievedQuests() {
        this.achievedQuests--;
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
     * Set total number of achieved quests for a specific category.
     *
     * @param category the category name.
     * @param i        number of achieved quests to set.
     */
    public void setTotalCategoryAchievedQuests(String category, int i) {
        this.totalAchievedQuestsByCategory.put(category, i);
    }

    /**
     * Set total achieved quests for all categories.
     *
     * @param totals a map of total achieved quests by category.
     */
    public void setTotalAchievedQuestsByCategory(Map<String, Integer> totals) {
        this.totalAchievedQuestsByCategory.clear();
        this.totalAchievedQuestsByCategory.putAll(totals);
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
     * Add the number of achieved quests for a specific category.
     *
     * @param category the category name.
     * @param amount   the number of quests to add.
     */
    public void addTotalCategoryAchievedQuests(String category, int amount) {
        if (this.totalAchievedQuestsByCategory.containsKey(category)) {
            final int newAmount = this.totalAchievedQuestsByCategory.get(category) + amount;
            this.totalAchievedQuestsByCategory.put(category, newAmount);
        } else {
            this.totalAchievedQuestsByCategory.put(category, amount);
        }
    }

    /**
     * Remove number of achieved quests.
     *
     * @param i number of achieved quests to remove.
     */
    public void removeTotalAchievedQuests(int i) {
        this.totalAchievedQuests = Math.max(this.totalAchievedQuests - i, 0);
    }

    /**
     * Subtract the number of achieved quests for a specific category.
     *
     * @param category the category name.
     * @param amount   the number of quests to subtract.
     */
    public void removeTotalCategoryAchievedQuests(String category, int amount) {
        if (this.totalAchievedQuestsByCategory.containsKey(category)) {
            final int toSet = Math.max(totalAchievedQuestsByCategory.get(category) - amount, 0);
            this.totalAchievedQuestsByCategory.put(category, toSet);
        } else {
            this.totalAchievedQuestsByCategory.put(category, 0);
        }
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
    public Map<AbstractQuest, Progression> getQuests() {
        return this.quests;
    }

    /**
     * Get total achieved quests for all categories.
     *
     * @return a map of total achieved quests by category.
     */
    public Map<String, Integer> getTotalAchievedQuestsByCategory() {
        return this.totalAchievedQuestsByCategory;
    }

    /**
     * Get the number of total achieved quests for a specific category.
     *
     * @param category the category name.
     * @return the number of total achieved quests for the specified category.
     */
    public int getTotalAchievedQuestsByCategory(String category) {
        return this.totalAchievedQuestsByCategory.getOrDefault(category, 0);
    }
}
