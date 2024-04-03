package com.ordwen.odailyquests.events.listeners.entity.custom.mobs;


import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobDeathListener extends AbstractEntityChecker implements Listener {

    @EventHandler
    public void onMythicMobsDeathEvent(MythicMobDeathEvent event) {
        if (event.getKiller() != null && event.getKiller() instanceof Player player) {
            setPlayerQuestProgression(player, null, event.getMobType().getInternalName(), 1, "CUSTOM_MOBS", null);
        }
    }
}
