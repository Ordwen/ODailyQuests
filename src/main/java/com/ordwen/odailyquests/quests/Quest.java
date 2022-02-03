package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.rewards.Reward;
import com.ordwen.odailyquests.rewards.RewardType;
import org.bukkit.Material;

public class Quest {

    int totalQuests = 0;

    String questName;
    String questDesc;
    QuestType questType;
    Material itemRequired;
    int amountRequired;
    Reward reward;

    /**
     * Quest constructor.
     * @param questType the type of the quest.
     */
    public Quest(String questName, String questDesc, QuestType questType, Material itemRequired, int amountRequired, Reward reward) {
        this.questName = questName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.itemRequired = itemRequired;
        this.amountRequired = amountRequired;
        this.reward = reward;
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
    public Material getItemRequired() {
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
}
