package com.ordwen.odailyquests.quests.player.progression.types;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class AbstractQuest {

    final int questIndex;
    final String questName;
    final List<String> questDesc;
    final QuestType questType;
    final ItemStack menuItem;
    final int amountRequired;
    final Reward reward;

    /**
     * Quest constructor.
     *
     * @param questName      name of the quest.
     * @param questDesc      description of the quest.
     * @param questType      type of the quest.
     * @param amountRequired required amount of the item.
     * @param reward         reward of the quest.
     */
    public AbstractQuest(int questIndex, String questName, List<String> questDesc, QuestType questType, ItemStack menuItem, int amountRequired, Reward reward) {
        this.questIndex = questIndex;
        this.questName = questName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.menuItem = menuItem;
        this.amountRequired = amountRequired;
        this.reward = reward;
    }

    /**
     * Quest constructor.
     *
     * @param globalQuest quest base.
     */
    public AbstractQuest(GlobalQuest globalQuest) {
        this.questIndex = globalQuest.questIndex;
        this.questName = globalQuest.questName;
        this.questDesc = globalQuest.questDesc;
        this.questType = globalQuest.questType;
        this.menuItem = globalQuest.menuItem;
        this.amountRequired = globalQuest.amountRequired;
        this.reward = globalQuest.reward;
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
    public QuestType getType() {
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
     * Get menu item.
     *
     * @return menu item.
     */
    public ItemStack getMenuItem() {
        return this.menuItem;
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
}
