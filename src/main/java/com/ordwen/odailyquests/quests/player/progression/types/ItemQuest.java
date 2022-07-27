package com.ordwen.odailyquests.quests.player.progression.types;

import org.bukkit.inventory.ItemStack;

public class ItemQuest extends AbstractQuest {

    final ItemStack requiredItem;

    /**
     * Quest constructor.
     *
     * @param globalQuest parent quest.
     * @param requiredItem  required item.
     */
    public ItemQuest(GlobalQuest globalQuest, ItemStack requiredItem) {
        super(globalQuest);
        this.requiredItem = requiredItem;
    }

    /**
     * Get the item required by the quest.
     *
     * @return quest item-required.
     */
    public ItemStack getRequiredItem() {
        return this.requiredItem;
    }
}
