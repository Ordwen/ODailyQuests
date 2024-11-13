package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileLaunchListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity().getShooter() instanceof Player player) {

            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("ProjectileLaunchListener: onProjectileLaunch summoned by " + player.getName() + " for " + event.getEntity().getType() + ".");

            setPlayerQuestProgression(event, player, 1, "LAUNCH");
        }
    }
}
