package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PickupItemListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof Player player)) return;

        final Item itemEntity = event.getItem();
        final ItemStack original = itemEntity.getItemStack();

        if (Antiglitch.isStoreDroppedItems() && original.hasItemMeta()) {
            ItemMeta meta = original.getItemMeta();
            if (meta == null) return;

            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            if (pdc.has(Antiglitch.DROPPED_KEY, PersistentDataType.STRING)) {
                Debugger.write("PickupItemListener: progression cancelled for " + player.getName() + " due to dropped item");

                pdc.remove(Antiglitch.DROPPED_KEY);
                original.setItemMeta(meta);

                itemEntity.setItemStack(original);

                return;
            }
        }

        Debugger.write("PickupItemListener: onPickupItemEvent summoned by " + player.getName() + " for " + original.getType() + ".");
        setPlayerQuestProgression(event, player, original.getAmount(), "PICKUP");
    }
}

