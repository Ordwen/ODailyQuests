package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.ODailyQuests;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getEntity().setMetadata("odailyquests:dead", new FixedMetadataValue(ODailyQuests.INSTANCE, true));
    }
}
