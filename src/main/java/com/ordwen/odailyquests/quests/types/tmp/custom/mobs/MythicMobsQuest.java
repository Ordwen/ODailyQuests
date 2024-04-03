package com.ordwen.odailyquests.quests.types.tmp.custom.mobs;

import com.ordwen.odailyquests.quests.types.BasicQuest;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class MythicMobsQuest extends CustomMobQuest {

    public MythicMobsQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "MYTHIC_MOBS";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof MythicMobDeathEvent event) {
            return super.isRequiredEntity(event.getMobType().getInternalName());
        }
        return false;
    }
}
