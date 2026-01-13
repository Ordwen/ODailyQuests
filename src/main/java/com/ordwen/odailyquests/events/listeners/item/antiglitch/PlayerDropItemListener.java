package com.ordwen.odailyquests.events.listeners.item.antiglitch;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerDropItemListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (!Antiglitch.isStoreDroppedItems()) return;

        final Item item = event.getItemDrop();
        final long until = System.currentTimeMillis() + Antiglitch.getDroppedItemsTtlMillis();

        item.getPersistentDataContainer().set(
                Antiglitch.DROP_UNTIL_KEY,
                PersistentDataType.LONG,
                until
        );
    }
}