package com.ordwen.odailyquests.quests.types.tmp;

import com.ordwen.odailyquests.quests.types.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityBreedEvent;

public class BreedQuest extends EntityQuest {

    public BreedQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "BREED";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof EntityBreedEvent event) {
            return super.isRequiredEntity(event.getEntity().getType());
        }

        return false;
    }
}
