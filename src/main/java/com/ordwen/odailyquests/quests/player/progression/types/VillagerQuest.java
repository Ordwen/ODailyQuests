package com.ordwen.odailyquests.quests.player.progression.types;

import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public class VillagerQuest extends AbstractQuest {

    final ItemStack requiredItem;
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

        this.requiredItem = null;
    }

    /**
     * Quest constructor for quest type TRADE (item specified).
     *
     * @param globalQuest        parent quest.
     * @param requiredItem       required item.
     * @param villagerProfession required villager profession.
     * @param villagerLevel      required villager level.
     */
    public VillagerQuest(GlobalQuest globalQuest, ItemStack requiredItem, Villager.Profession villagerProfession, int villagerLevel) {
        super(globalQuest);
        this.requiredItem = requiredItem;
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
    public ItemStack getRequiredItem() {
        return this.requiredItem;
    }

}
