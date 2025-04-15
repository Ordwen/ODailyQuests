package com.ordwen.odailyquests.quests.types.entity;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
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
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof EntityBreedEvent event) {
            return super.isRequiredEntity(event.getEntity().getType(), progression);
        }

        return false;
    }
}
