package com.ordwen.odailyquests.events.listeners.crate;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

public class CrateOpenListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCrateOpenEvent(CrateOpenEvent event) {
        Debugger.write("CrateOpenListener: onCrateOpenEvent summoned for " + event.getPlayer().getName() + ".");
        if (event.isCancelled()) {
            Debugger.write("CrateOpenListener: onCrateOpenEvent is cancelled.");
            return;
        }

        setPlayerQuestProgression(event, event.getPlayer(), 1, "CRATE_OPEN");
    }
}
