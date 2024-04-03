package com.ordwen.odailyquests.quests.types.tmp.custom.mobs;

import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.ordwen.odailyquests.quests.types.BasicQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class EliteMobsQuest extends CustomMobQuest {

    public EliteMobsQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "ELITE_MOBS";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof EliteMobDeathEvent event) {
            return super.isRequiredEntity(event.getEliteEntity().getName().substring(event.getEliteEntity().getName().indexOf(' ') + 1));
        }
        return false;
    }
}
