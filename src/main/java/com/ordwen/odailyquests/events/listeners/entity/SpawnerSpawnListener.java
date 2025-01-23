package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.functionalities.SpawnersProgression;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SpawnerSpawnEvent;

public class SpawnerSpawnListener implements Listener {

    @EventHandler
    public void onSpawnerSpawnEvent(SpawnerSpawnEvent event) {
        if (event.isCancelled()) return;

        Debugger.addDebug("Spawner spawn event: " + event.getEntity().getType());
        if (SpawnersProgression.isSpawnersProgressionDisabled()) {
            EntitySource.addEntityFromSpawner(event.getEntity());
        }
    }
}
