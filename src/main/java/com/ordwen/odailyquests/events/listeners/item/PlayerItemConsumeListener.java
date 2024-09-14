package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsumeListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("PlayerItemConsumeListener: onItemConsumeEvent summoned by " + event.getPlayer().getName() + " for " + event.getItem().getType() + ".");

        setPlayerQuestProgression(event, event.getPlayer(), 1, "CONSUME");
    }

    @EventHandler
    public void onResurrect(EntityResurrectEvent event) {
        if (event.isCancelled()) {
            Debugger.addDebug("PlayerItemConsumeListener: onResurrect cancelled.");
            return;
        }

        if (!(event.getEntity() instanceof Player player)) return;
        setPlayerQuestProgression(event, player, 1, "CONSUME");
    }
}
