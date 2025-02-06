package com.ordwen.odailyquests.events.listeners.crate;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import su.nightexpress.excellentcrates.api.event.CrateOpenEvent;

public class CrateOpenListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onCrateOpenEvent(CrateOpenEvent event) {
        setPlayerQuestProgression(event, event.getPlayer(), 1, "CRATE_OPEN");
    }
}
