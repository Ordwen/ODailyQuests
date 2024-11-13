package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ShearEntityListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onShearEntityEvent(PlayerShearEntityEvent event) {
        if (event.isCancelled()) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("ShearEntityListener: onShearEntityEvent summoned by " + event.getPlayer().getName() + " for " + event.getEntity().getType() + ".");

        setPlayerQuestProgression(event, event.getPlayer(), 1, "SHEAR");
    }
}
