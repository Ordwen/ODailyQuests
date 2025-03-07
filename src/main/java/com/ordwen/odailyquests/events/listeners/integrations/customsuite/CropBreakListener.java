package com.ordwen.odailyquests.events.listeners.integrations.customsuite;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import net.momirealms.customcrops.api.event.CropBreakEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CropBreakListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onCropBreak(CropBreakEvent event) {
        final Entity breaker = event.entityBreaker();

        if (breaker instanceof Player player) {
            Debugger.write("CropBreakListener: onCropBreak summoned by " + player.getName() + ".");
            setPlayerQuestProgression(event, player, 1, "BREAK");
        }
    }
}
