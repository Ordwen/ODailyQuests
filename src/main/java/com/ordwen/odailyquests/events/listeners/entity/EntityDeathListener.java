package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;

import com.ordwen.odailyquests.externs.hooks.mobs.MythicMobsHook;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        if (MythicMobsHook.isMythicMobsSetup()) {
            final ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
            if (mythicMob != null) return;
        }

        if (EntitySource.isEntityFromSpawner(entity)) {
            Debugger.addDebug("[EntityDeathEvent] Entity is from spawner, cancelling progression.");
            return;
        }

        if (WildStackerEnabled.isEnabled()) return;
        if (entity.getKiller() == null) return;

        Debugger.addDebug("EntityDeathListener: onEntityDeathEvent summoned by " + entity.getKiller().getName() + " for " + entity.getType() + ".");

        setPlayerQuestProgression(event, entity.getKiller(), 1, "KILL");
    }
}
