package com.ordwen.odailyquests.quest;

import org.bukkit.inventory.ItemStack;

public class Quest {

    int totalQuests = 0;

    int questNumber;
    String questName;
    String questDesc;
    Type type;
    ItemStack itemRequired;
    int amountRequired;
    /* reward */

    /**
     * Quest constructor.
     * @param type the type of the quest.
     */
    public Quest(Type type) {
        this.type = type;
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
    public Type getType() {
        return this.type;
    }
}
