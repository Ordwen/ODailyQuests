package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.BasicQuest;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemQuest extends AbstractQuest {

    final List<ItemStack> requiredItems;
    final boolean ignoreNbt;

    /**
     * Quest constructor.
     *
     * @param basicQuest   parent quest.
     * @param requiredItems list of required items.
     */
    public ItemQuest(BasicQuest basicQuest, List<ItemStack> requiredItems, boolean ignoreNbt) {
        super(basicQuest);
        this.requiredItems = requiredItems;
        this.ignoreNbt = ignoreNbt;
    }

    /**
     * Get the item required by the quest.
     *
     * @return quest item-required.
     */
    public List<ItemStack> getRequiredItems() {
        return this.requiredItems;
    }

    /**
     * Check if the quest should ignore NBT.
     *
     * @return true if the quest should ignore NBT, false otherwise.
     */
    public boolean isIgnoreNbt() {
        return this.ignoreNbt;
    }
}
