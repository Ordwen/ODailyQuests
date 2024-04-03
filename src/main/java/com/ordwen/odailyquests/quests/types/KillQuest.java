package com.ordwen.odailyquests.quests.types;

import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class KillQuest extends EntityQuest implements Listener {

    public KillQuest(BasicQuest base) {
        super(base);
    }

    public String getType() {
        return "KILL";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof EntityDeathEvent event) {
            final LivingEntity entity = event.getEntity();

            boolean isRequiredEntity = false;
            if (requiredEntities == null) isRequiredEntity = true;
            else {
                for (EntityType type : requiredEntities) {
                    isRequiredEntity = type.equals(entity.getType());
                    if (isRequiredEntity) break;
                }
            }

            return true;

            /*
            if (isRequiredEntity) {
                setPlayerQuestProgression(entity.getKiller(), 1, "KILL");
            }

             */
        }

        return false;
    }
}
