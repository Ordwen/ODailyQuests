package com.ordwen.odailyquests.events.listeners.entity.custom.mobs;


import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobDeathListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onMythicMobsDeathEvent(MythicMobDeathEvent event) {
        if (event.getKiller() != null && event.getKiller() instanceof Player player) {
            setPlayerQuestProgression(event, player, 1, "CUSTOM_MOBS");
        }
    }
}
