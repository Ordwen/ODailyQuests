package com.ordwen.odailyquests.events.listeners.entity.custom.stackers;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.RoseStackerEnabled;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.event.EntityStackMultipleDeathEvent;
import dev.rosewood.rosestacker.event.EntityUnstackEvent;
import dev.rosewood.rosestacker.stack.StackedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class RoseStackerListener extends PlayerProgressor implements Listener {

    /*
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRoseStackerEntityUnstackEvent(EntityUnstackEvent event) {
        if (!RoseStackerEnabled.isEnabled()) return;
        if (event.isCancelled()) return;

        final StackedEntity previous = event.getStack();
        final StackedEntity current = event.getResult();

        final Entity entity = previous.getEntity();

        if (EntitySource.isEntityFromSpawner(entity)) {
            Debugger.write("[EntityUnstackListener] Entity is from spawner, cancelling progression.");
            return;
        }

        final Player player = previous.getEntity().getKiller();
        if (player == null) return;

        final int amount = previous.getStackSize() - current.getStackSize();

        setPlayerQuestProgression(event, player, amount, "KILL");
    }
     */

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRoseStackerEntityUnstackEvent(EntityStackMultipleDeathEvent event) {
        if (!RoseStackerEnabled.isEnabled()) return;

        final Entity entity = event.getStack().getEntity();

        if (EntitySource.isEntityFromSpawner(entity)) {
            Debugger.write("EntityStackMultipleDeathEvent: Entity is from spawner, cancelling progression.");
            return;
        }

        final Player player = event.getStack().getEntity().getKiller();
        if (player == null) return;

        Debugger.write("EntityStackMultipleDeathEvent: onEntityUnstackEvent summoned by " + player.getName() + " for " + entity.getType() + ".");
        setPlayerQuestProgression(event, player, event.getEntityKillCount(), "KILL");
    }
}
