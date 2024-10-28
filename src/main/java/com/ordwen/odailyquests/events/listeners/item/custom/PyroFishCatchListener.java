package com.ordwen.odailyquests.events.listeners.item.custom;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PyroFishCatchListener extends PlayerProgressor implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPyroFishCatch(PyroFishCatchEvent event) {
        setPlayerQuestProgression(event, event.getPlayer(), 1, "PYRO_FISH");
    }
}
