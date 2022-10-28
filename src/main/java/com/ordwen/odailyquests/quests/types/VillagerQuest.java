package com.ordwen.odailyquests.quests.types;

import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class VillagerQuest extends AbstractQuest {

    final List<ItemStack> requiredItems;
    final Villager.Profession villagerProfession;
    final int villagerLevel;

    /**
     * Quest constructor for quest type TRADE (global).
     *
     * @param globalQuest        parent quest.
     * @param villagerProfession required villager profession.
     * @param villagerLevel      required villager level.
     */
    public VillagerQuest(GlobalQuest globalQuest, Villager.Profession villagerProfession, int villagerLevel) {
        super(globalQuest);
        this.villagerProfession = villagerProfession;
        this.villagerLevel = villagerLevel;

        this.requiredItems = null;
    }

    /**
     * Quest constructor for quest type TRADE (item specified).
     *
     * @param globalQuest        parent quest.
     * @param requiredItems       required item.
     * @param villagerProfession required villager profession.
     * @param villagerLevel      required villager level.
     */
    public VillagerQuest(GlobalQuest globalQuest, List<ItemStack> requiredItems, Villager.Profession villagerProfession, int villagerLevel) {
        super(globalQuest);
        this.requiredItems = requiredItems;
        this.villagerProfession = villagerProfession;
        this.villagerLevel = villagerLevel;
    }

    /**
     * Get required villager profession
     *
     * @return villager profession
     */
    public Villager.Profession getVillagerProfession() {
        return this.villagerProfession;
    }

    /**
     * Get required villager level
     *
     * @return villager level
     */
    public int getVillagerLevel() {
        return this.villagerLevel;
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
