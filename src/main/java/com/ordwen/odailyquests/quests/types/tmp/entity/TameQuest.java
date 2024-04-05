package com.ordwen.odailyquests.quests.types.tmp.entity;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTameEvent;

public class TameQuest extends EntityQuest {

    public TameQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "TAME";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof EntityTameEvent event) {
            return super.isRequiredEntity(event.getEntity().getType());
        }

        return false;
    }
}
