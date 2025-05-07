package com.ordwen.odailyquests.quests.types.entity;

import com.bgsoftware.wildstacker.api.events.EntityUnstackEvent;
import com.ordwen.odailyquests.configuration.integrations.RoseStackerEnabled;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent;
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
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof EntityDeathEvent event) {
            return super.isRequiredEntity(event.getEntity().getType(), progression);
        }

        else if (WildStackerEnabled.isEnabled() && provided instanceof EntityUnstackEvent event) {
            return super.isRequiredEntity(event.getEntity().getType(), progression);
        }

        else if (RoseStackerEnabled.isEnabled() && provided instanceof EntityStackMultipleDeathEvent event) {
            return super.isRequiredEntity(event.getStack().getEntity().getType(), progression);
        }

        return false;
    }
}
