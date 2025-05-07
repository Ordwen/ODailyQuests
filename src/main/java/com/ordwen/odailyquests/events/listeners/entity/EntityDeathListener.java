package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.RoseStackerEnabled;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.tools.PluginUtils;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.stack.StackedEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeathEvent(EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        if (PluginUtils.isPluginEnabled("MythicMobs")) {
            final ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
            if (mythicMob != null) return;
        }

        if (EntitySource.isEntityFromSpawner(entity)) {
            Debugger.write("[EntityDeathEvent] Entity is from spawner, cancelling progression.");
            return;
        }

        if (WildStackerEnabled.isEnabled()) return;

        if (RoseStackerEnabled.isEnabled()) {
            final StackedEntity stacked = RoseStackerAPI.getInstance().getStackedEntity(entity);
            if (stacked != null && stacked.areMultipleEntitiesDying(event)) return;
        }

        if (entity.getKiller() == null) return;

        Debugger.write("EntityDeathListener: onEntityDeathEvent summoned by " + entity.getKiller().getName() + " for " + entity.getType() + ".");

        setPlayerQuestProgression(event, entity.getKiller(), 1, "KILL");
    }
}
