package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class PlayerLevelChangeListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onPlayerLevelChangeEvent(PlayerLevelChangeEvent event) {
        final int diff = event.getNewLevel() - event.getOldLevel();
        if (diff > 0) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("PlayerLevelChangeListener: onPlayerLevelChangeEvent summoned by " + event.getPlayer().getName());

            setPlayerQuestProgression(event, event.getPlayer(), diff, "EXP_LEVELS");
        }
    }
}
