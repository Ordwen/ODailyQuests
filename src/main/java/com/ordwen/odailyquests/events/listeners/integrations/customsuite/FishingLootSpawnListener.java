package com.ordwen.odailyquests.events.listeners.integrations.customsuite;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FishingLootSpawnListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onLootSpawn(FishingLootSpawnEvent event) {
        Debugger.addDebug("FishingLootSpawnListener: onLootSpawn summoned by " + event.getPlayer().getName());
        setPlayerQuestProgression(event, event.getPlayer(), 1, "FISH");
    }
}
