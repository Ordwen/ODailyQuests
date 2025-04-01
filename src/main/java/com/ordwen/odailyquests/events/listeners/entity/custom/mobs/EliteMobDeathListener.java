package com.ordwen.odailyquests.events.listeners.entity.custom.mobs;

import com.magmaguy.elitemobs.api.EliteMobDeathEvent;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EliteMobDeathListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEliteMobsDeathEvent(EliteMobDeathEvent event) {
        final Player player = event.getEntityDeathEvent().getEntity().getKiller();
        if (player != null) {
            setPlayerQuestProgression(event, player, 1, "ELITE_MOBS");
        }
    }
}
