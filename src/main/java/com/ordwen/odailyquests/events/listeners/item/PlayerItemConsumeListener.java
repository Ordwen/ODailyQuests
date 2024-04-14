package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsumeListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("PlayerItemConsumeListener: onItemConsumeEvent summoned by " + event.getPlayer().getName() + " for " + event.getItem().getType() + ".");

        setPlayerQuestProgression(event, event.getPlayer(), 1, "CONSUME");
    }
}
