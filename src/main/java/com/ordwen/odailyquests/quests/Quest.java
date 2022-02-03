package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.rewards.RewardType;
import org.bukkit.inventory.ItemStack;

public class Quest {

    int totalQuests = 0;

    String questName;
    String questDesc;
    QuestType questType;
    ItemStack itemRequired;
    int amountRequired;
    RewardType rewardType;
    int amountRewarded;

    /**
     * Quest constructor.
     * @param questType the type of the quest.
     */
    public Quest(String questName, String questDesc, QuestType questType, ItemStack itemRequired, int amountRequired, RewardType rewardType, int amountRewarded) {
        this.questName = questName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.itemRequired = itemRequired;
        this.amountRequired = amountRequired;
        this.rewardType = rewardType;
        this.amountRewarded = amountRewarded;
    }

    /**
     * Get total number of quests.
     * @return number of quests.
     */
    public final int getNumberOfQuests() {
        return totalQuests;
    }

    /**
     * Get the type of a quest.
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
    public String getQuestDesc() {
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
     * Get the reward type of the quest.
     * @return quest reward-type.
     */
    public RewardType getRewardType() {
        return this.rewardType;
    }

    /**
     * Get the amount rewarded of the quest.
     * @return quest amount-rewarded.
     */
    public int getAmountRewarded() {
        return this.amountRewarded;
    }
}
