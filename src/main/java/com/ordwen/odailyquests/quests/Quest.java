package com.ordwen.odailyquests.quests;

import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Quest {

    int questIndex;
    String questName;
    List<String> questDesc;
    QuestType questType;
    ItemStack itemRequired;
    ItemStack menuItem;
    EntityType entityType;
    int amountRequired;
    Reward reward;

    /**
     * Quest constructor (for itemstack type).
     * @param questName name of the quest.
     * @param questDesc description of the quest.
     * @param questType type of the quest.
     * @param itemRequired required item to complete the quest.
     * @param amountRequired required amount of the item.
     * @param reward reward of the quest.
     */
    public Quest(int questIndex, String questName, List<String> questDesc, QuestType questType, ItemStack itemRequired, ItemStack menuItem, int amountRequired, Reward reward) {
        this.questIndex = questIndex;
        this.questName = questName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.itemRequired = itemRequired;
        this.menuItem = menuItem;
        this.amountRequired = amountRequired;
        this.reward = reward;
    }

    /**
     * Quest constructor (for entity type).
     * @param questName name of the quest.
     * @param questDesc description of the quest.
     * @param questType type of the quest.
     * @param entityType required entity to complete the quest.
     * @param amountRequired required amount of the item.
     * @param reward reward of the quest.
     */
    public Quest(int questIndex, String questName, List<String> questDesc, QuestType questType, EntityType entityType, ItemStack menuItem, int amountRequired, Reward reward) {
        this.questIndex = questIndex;
        this.questName = questName;
        this.questDesc = questDesc;
        this.questType = questType;
        this.entityType = entityType;
        this.menuItem = menuItem;
        this.amountRequired = amountRequired;
        this.reward = reward;
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
     * Get menu item.
     * @return menu item.
     */
    public ItemStack getMenuItem() {
        return this.menuItem;
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
     * Get the entity required by the quest.
     * @return quest item-required.
     */
    public EntityType getEntityType() {
        return this.entityType;
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
