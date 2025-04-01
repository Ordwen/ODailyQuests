package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerExpChangeListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
        Debugger.write("PlayerExpChangeListener: onPlayerExpChangeEvent summoned by " + event.getPlayer().getName());
        setPlayerQuestProgression(event, event.getPlayer(), event.getAmount(), "EXP_POINTS");
    }
}
