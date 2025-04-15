package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.events.customs.CustomFurnaceExtractEvent;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.ItemStack;

public class CookQuest extends ItemQuest {

    public CookQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "COOK";
    }

    @Override
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof FurnaceExtractEvent event) {
            return super.isRequiredItem(new ItemStack(event.getItemType()), progression);
        }

        if (provided instanceof CustomFurnaceExtractEvent event) {
            return super.isRequiredItem(new ItemStack(event.getResult().getType()), progression);
        }

        return false;
    }
}
