package com.ordwen.odailyquests.events.listeners.item.antiglitch;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

public class ItemDespawnListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDespawn(ItemDespawnEvent event) {
        event.getEntity().getPersistentDataContainer().remove(Antiglitch.DROP_UNTIL_KEY);
    }
}
