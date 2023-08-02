package com.ordwen.odailyquests.quests.types;

import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractQuest {

    final int questIndex;
    final String questName;
    final String categoryName;
    final List<String> questDesc;
    final QuestType questType;
    final ItemStack menuItem;
    final ItemStack achievedItem;
    final int amountRequired;
    final Reward reward;
    final List<String> requiredWorlds;
    final boolean isUsingPlaceholders;

    /**
     * Quest constructor.
     *
     * @param questName      name of the quest.
     * @param questDesc      description of the quest.
     * @param questType      type of the quest.
     * @param amountRequired required amount of the item.
     * @param reward         reward of the quest.
     */
    public AbstractQuest(int questIndex, String questName, String categoryName, List<String> questDesc, QuestType questType, ItemStack menuItem, ItemStack achievedItem, int amountRequired, Reward reward, List<String> requiredWorlds, boolean isUsingPlaceholders) {
        this.questIndex = questIndex;
        this.questName = questName;
        this.categoryName = categoryName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.menuItem = menuItem;
        this.achievedItem = achievedItem;
        this.amountRequired = amountRequired;
        this.reward = reward;
        this.requiredWorlds = requiredWorlds;
        this.isUsingPlaceholders = isUsingPlaceholders;
    }

    /**
     * Quest constructor.
     *
     * @param globalQuest quest base.
     */
    public AbstractQuest(GlobalQuest globalQuest) {
        this.questIndex = globalQuest.questIndex;
        this.questName = globalQuest.questName;
        this.categoryName = globalQuest.categoryName;
        this.questDesc = globalQuest.questDesc;
        this.questType = globalQuest.questType;
        this.menuItem = globalQuest.menuItem;
        this.achievedItem = globalQuest.achievedItem;
        this.amountRequired = globalQuest.amountRequired;
        this.reward = globalQuest.reward;
        this.requiredWorlds = globalQuest.requiredWorlds;
        this.isUsingPlaceholders = globalQuest.isUsingPlaceholders;
    }

    /**
     * Get index of quest.
     *
     * @return quest index.
     */
    public int getQuestIndex() {
        return this.questIndex;
    }

    /**
     * Get the type of quest.
     *
     * @return the type of the quest.
     */
    public QuestType getQuestType() {
        return this.questType;
    }

    /**
     * Get the name of the quest.
     *
     * @return quest name.
     */
    public String getQuestName() {
        return this.questName;
    }

    /**
     * Get the name of the category.
     *
     * @return category name.
     */
    public String getCategoryName() {
        return this.categoryName;
    }

    /**
     * Get menu item.
     *
     * @return menu item.
     */
    public ItemStack getMenuItem() {
        return this.menuItem;
    }

    /**
     * Get achieved item.
     *
     * @return achieved item.
     */
    public ItemStack getAchievedItem() {
        return this.achievedItem;
    }

    /**
     * Get the description of the quest.
     *
     * @return quest description.
     */
    public List<String> getQuestDesc() {
        return this.questDesc;
    }

    /**
     * Get the amount required by the quest.
     *
     * @return quest amount-required.
     */
    public int getAmountRequired() {
        return this.amountRequired;
    }

    /**
     * Get the reward of the quest.
     *
     * @return quest reward.
     */
    public Reward getReward() {
        return this.reward;
    }

    /**
     * Get the required worlds of the quest.
     *
     * @return quest required worlds.
     */
    public List<String> getRequiredWorlds() {
        return this.requiredWorlds;
    }

    /**
     * Get whether the quest is using placeholders.
     *
     * @return quest isUsingPlaceholders.
     */
    public boolean isUsingPlaceholders() {
        return this.isUsingPlaceholders;
    }
}
