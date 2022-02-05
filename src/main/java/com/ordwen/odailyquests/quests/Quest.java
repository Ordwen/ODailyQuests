package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Quest {

    public int totalQuests = 0;

    String questName;
    List<String> questDesc;
    QuestType questType;
    ItemStack itemRequired;
    int amountRequired;
    Reward reward;

    /**
     * Quest constructor.
     * @param questType the type of the quest.
     * @param itemRequired
     */
    public Quest(String questName, List<String> questDesc, QuestType questType, ItemStack itemRequired, int amountRequired, Reward reward) {
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
}
