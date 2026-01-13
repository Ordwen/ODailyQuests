package com.ordwen.odailyquests.events.listeners.item.antiglitch;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemMergeListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemMerge(ItemMergeEvent event) {
        if (!Antiglitch.isStoreDroppedItems()) {
            return;
        }

        final Item source = event.getEntity();
        final Item target = event.getTarget();

        final PersistentDataContainer src = source.getPersistentDataContainer();
        final PersistentDataContainer tgt = target.getPersistentDataContainer();

        final Long a = src.get(Antiglitch.DROP_UNTIL_KEY, PersistentDataType.LONG);
        final Long b = tgt.get(Antiglitch.DROP_UNTIL_KEY, PersistentDataType.LONG);

        if (a == null && b == null) {
            return;
        }

        // set the later drop until time on the merged item
        final long until = Math.max(a != null ? a : 0L, b != null ? b : 0L);
        tgt.set(Antiglitch.DROP_UNTIL_KEY, PersistentDataType.LONG, until);
        src.remove(Antiglitch.DROP_UNTIL_KEY);
    }
}
