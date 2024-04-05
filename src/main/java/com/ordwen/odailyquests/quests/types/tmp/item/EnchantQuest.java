package com.ordwen.odailyquests.quests.types.tmp.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.event.Event;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantQuest extends ItemQuest {

    public EnchantQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "ENCHANT";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof EnchantItemEvent event) {
            return super.isRequiredItem(event.getItem());
        }

        return false;
    }
}
