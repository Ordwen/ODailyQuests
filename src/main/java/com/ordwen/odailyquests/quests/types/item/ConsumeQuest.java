package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class ConsumeQuest extends ItemQuest {

    public ConsumeQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "CONSUME";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof PlayerItemConsumeEvent event) {
            return super.isRequiredItem(event.getItem());
        }

        return false;
    }
}
