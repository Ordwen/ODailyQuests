package com.ordwen.odailyquests.quests.types.tmp.entity;

import com.ordwen.odailyquests.quests.types.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillQuest extends EntityQuest {

    public KillQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "KILL";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof EntityDeathEvent event) {
            return super.isRequiredEntity(event.getEntity().getType());
        }

        return false;
    }
}
