package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.externs.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener extends AbstractEntityChecker implements Listener {

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

        setPlayerQuestProgression(entity.getKiller(), event.getEntityType(), null, 1, QuestType.KILL, null);
    }
}
