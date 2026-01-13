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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PickupItemListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        final Item itemEntity = event.getItem();
        final ItemStack stack = itemEntity.getItemStack();

        if (Antiglitch.isStoreDroppedItems()) {
            final PersistentDataContainer pdc = itemEntity.getPersistentDataContainer();
            final Long until = pdc.get(Antiglitch.DROP_UNTIL_KEY, PersistentDataType.LONG);

            if (until != null) {
                if (until >= System.currentTimeMillis()) {
                    Debugger.write("PickupItemListener: progression cancelled for " + player.getName() + " due to recently dropped item");
                    return; // let the pickup be ignored
                }
                // cleanup the metadata
                pdc.remove(Antiglitch.DROP_UNTIL_KEY);
            }
        }

        Debugger.write("PickupItemListener: onPickupItemEvent summoned by " + player.getName() + " for " + stack.getType() + ".");
        setPlayerQuestProgression(event, player, stack.getAmount(), "PICKUP");
    }
}
