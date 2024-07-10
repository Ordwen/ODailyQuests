package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;

public class VillagerQuest extends ItemQuest {

    Villager.Profession profession;
    int level;

    public VillagerQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "VILLAGER_TRADE";
    }

    @Override
    public boolean canProgress(Event provided) {
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        if (!super.loadParameters(section, file, index)) return false;

        /* check if the item have to be obtained by a villager */
        if (section.contains(".villager_profession")) {
            profession = Villager.Profession.valueOf(section.getString(".villager_profession"));
        }
        if (section.contains(".villager_level")) {
            level = section.getInt(".villager_level");
        }

        return true;
    }

    /**
     * Get required villager profession
     *
     * @return villager profession
     */
    public Villager.Profession getVillagerProfession() {
        return this.profession;
    }

    /**
     * Get required villager level
     *
     * @return villager level
     */
    public int getVillagerLevel() {
        return this.level;
    }
}
