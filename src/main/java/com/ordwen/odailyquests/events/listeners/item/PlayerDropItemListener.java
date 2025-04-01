package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerDropItemListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        if (event.isCancelled()) return;

        if (Antiglitch.isStoreDroppedItems()) {
            final ItemStack item = event.getItemDrop().getItemStack();
            final ItemMeta itemMeta = item.getItemMeta();

            if (itemMeta != null) {
                final PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                pdc.set(Antiglitch.DROPPED_KEY, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
                item.setItemMeta(itemMeta);
            }
        }
    }
}
