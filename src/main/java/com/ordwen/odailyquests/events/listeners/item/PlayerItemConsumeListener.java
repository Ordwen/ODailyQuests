package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class PlayerItemConsumeListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent event) {
        if (event.isCancelled()) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("PlayerItemConsumeListener: onItemConsumeEvent summoned by " + event.getPlayer().getName() + " for " + event.getItem().getType() + ".");

        setPlayerQuestProgression(event.getPlayer(), event.getItem(), 1, "CONSUME");
    }
}
