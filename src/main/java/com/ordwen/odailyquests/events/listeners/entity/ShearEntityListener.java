package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class ShearEntityListener extends AbstractEntityChecker implements Listener {

    @EventHandler
    public void onShearEntityEvent(PlayerShearEntityEvent event) {
        if (event.isCancelled()) return;

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("ShearEntityListener: onShearEntityEvent summoned by " + event.getPlayer().getName() + " for " + event.getEntity().getType() + ".");

        final Entity entity = event.getEntity();
        if (entity instanceof Sheep sheep) {
            setPlayerQuestProgression(event.getPlayer(), event.getEntity().getType(), null, 1, "SHEAR", sheep.getColor());
        }
        else setPlayerQuestProgression(event.getPlayer(), event.getEntity().getType(), null, 1, "SHEAR", null);
    }
}
