package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Quest {

    public int totalQuests = 0;

    int questIndex;
    String questName;
    List<String> questDesc;
    QuestType questType;
    ItemStack itemRequired;
    int amountRequired;
    Reward reward;

    /**
     * Quest constructor.
     * @param questName name of the quest.
     * @param questDesc description of the quest.
     * @param questType type of the quest.
     * @param itemRequired required item to complete the quest.
     * @param amountRequired required amount of the item.
     * @param reward reward of the quest.
     */
    public Quest(int questIndex, String questName, List<String> questDesc, QuestType questType, ItemStack itemRequired, int amountRequired, Reward reward) {
        this.questIndex = questIndex;
        this.questName = questName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.itemRequired = itemRequired;
        this.amountRequired = amountRequired;
        this.reward = reward;

        totalQuests++;
    }

    /**
     * Get total number of quests.
     * @return number of quests.
     */
    public final int getNumberOfQuests() {
        return totalQuests;
    }

    /**
     * Get the type of quest.
     * @return the type of the quest.
     */
    public QuestType getType() {
        return this.questType;
    }

    /**
     * Get the name of the quest.
     * @return quest name.
     */
    public String getQuestName() {
        return this.questName;
    }

    /**
     * Get the description of the quest.
     * @return quest description.
     */
    public List<String> getQuestDesc() {
        return this.questDesc;
    }

    /**
     * Get the item required by the quest.
     * @return quest item-required.
     */
    public ItemStack getItemRequired() {
        return this.itemRequired;
    }

    /**
     * Get the amount required by the quest.
     * @return quest amount-required.
     */
    public int getAmountRequired() {
        return this.amountRequired;
    }

    /**
     * Get the reward of the quest.
     * @return quest reward.
     */
    public Reward getReward() {
        return this.reward;
    }

    /**
     * Get index of quest.
     * @return quest index.
     */
    public int getQuestIndex() {
        return this.questIndex;
    }
}
