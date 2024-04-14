package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class EntityTameListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onEntityTameEvent(EntityTameEvent event) {
        if (event.isCancelled()) return;

        if (event.getOwner() instanceof Player player) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("EntityTameEvent: onEntityTameEvent summoned by " + player.getName() + " for " + event.getEntityType() + ".");

            setPlayerQuestProgression(event, player, 1, "TAME");
        }
    }
}
