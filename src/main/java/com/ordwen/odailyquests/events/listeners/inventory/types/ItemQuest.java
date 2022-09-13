package com.ordwen.odailyquests.events.listeners.inventory.types;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemQuest extends AbstractQuest {

    final List<ItemStack> requiredItems;

    /**
     * Quest constructor.
     *
     * @param globalQuest parent quest.
     * @param requiredItems list of required items.
     */
    public ItemQuest(GlobalQuest globalQuest, List<ItemStack> requiredItems) {
        super(globalQuest);
        this.requiredItems = requiredItems;
    }

    /**
     * Get the item required by the quest.
     *
     * @return quest item-required.
     */
    public List<ItemStack> getRequiredItems() {
        return this.requiredItems;
    }
}
