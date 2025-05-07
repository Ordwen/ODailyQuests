package com.ordwen.odailyquests.events.listeners.entity.custom.stackers;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.RoseStackerEnabled;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class RoseStackerListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRoseStackerEntityUnstackEvent(EntityStackMultipleDeathEvent event) {
        if (!RoseStackerEnabled.isEnabled()) {
            return;
        }

        final Entity entity = event.getStack().getEntity();

        if (EntitySource.isEntityFromSpawner(entity)) {
            Debugger.write("EntityStackMultipleDeathEvent: Entity is from spawner, cancelling progression.");
            return;
        }

        if (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent && damageEvent.getDamager() instanceof Player player) {
            Debugger.write("EntityStackMultipleDeathEvent: onEntityUnstackEvent summoned by " + player.getName() + " for " + entity.getType() + ".");

            Bukkit.getScheduler().runTask(ODailyQuests.INSTANCE, () ->
                    setPlayerQuestProgression(event, player, event.getEntityKillCount(), "KILL")
            );
        }
    }
}
