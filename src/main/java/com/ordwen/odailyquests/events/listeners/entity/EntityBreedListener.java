package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class EntityBreedListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onEntityBreadEvent(EntityBreedEvent event) {
        if (event.isCancelled()) return;

        if (event.getBreeder() != null && event.getBreeder() instanceof Player player) {
            Debugger.write("=========================================================================================");
            Debugger.write("EntityBreedEvent: onEntityBreadEvent summoned by " + player.getName() + " for " + event.getEntityType() + ".");
            setPlayerQuestProgression(event, player, 1, "BREED");
        }
    }
}
