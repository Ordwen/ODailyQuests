package com.ordwen.odailyquests.events.listeners.entity.custom.stackers;

import com.bgsoftware.wildstacker.api.events.EntityUnstackEvent;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;

import com.ordwen.odailyquests.events.antiglitch.EntitySource;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class WildStackerListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWildStackerEntityUnstackEvent(EntityUnstackEvent event) {
        if (!WildStackerEnabled.isEnabled()) return;
        if (event.isCancelled()) return;

        final Entity entity = event.getEntity().getLivingEntity();

        if (EntitySource.isEntityFromSpawner(entity)) {
            Debugger.write("[EntityUnstackListener] Entity is from spawner, cancelling progression.");
            return;
        }

        if (event.getUnstackSource() == null) return;

        if (event.getUnstackSource() instanceof Player player) {
            setPlayerQuestProgression(event, player, event.getAmount(), "KILL");
        }
    }
}
