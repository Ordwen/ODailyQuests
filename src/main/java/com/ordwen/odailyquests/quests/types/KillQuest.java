package com.ordwen.odailyquests.quests.types;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;
import com.ordwen.odailyquests.externs.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.quests.types.shared.EntityQuest;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
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
    public void canProgress(LivingEntity entity) {
        boolean isRequiredEntity = false;
        if (requiredEntities == null) isRequiredEntity = true;
        else {
            for (EntityType type : requiredEntities) {
                isRequiredEntity = type.equals(entity.getType());
                if (isRequiredEntity) break;
            }
        }

        if (isRequiredEntity) {
            setPlayerQuestProgression(entity.getKiller(), 1, "KILL");
        }
    }

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        if (MythicMobsHook.isMythicMobsSetup()) {
            final ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
            if (mythicMob != null) return;
        }

        final boolean isEntityFromSpawner = EntitySource.isEntityFromSpawner(entity);
        EntitySource.removeEntityFromSpawner(entity);

        if (WildStackerEnabled.isEnabled()) return;
        if (entity.getKiller() == null) return;
        if (isEntityFromSpawner) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("EntityDeathListener: onEntityDeathEvent summoned by " + entity.getKiller().getName() + " for " + entity.getType() + ".");

        progressionHandler(entity);
    }
}
