package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PickupItemListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {

            final ItemStack item = event.getItem().getItemStack();

            if (Antiglitch.isStoreDroppedItems()) {
                if (item.hasItemMeta()) {
                    final PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                    final String droppedKey = pdc.get(Antiglitch.DROPPED_KEY, PersistentDataType.STRING);

                    if (droppedKey != null) return;
                }
            }

            Debugger.write("=========================================================================================");
            Debugger.write("PickupItemListener: onPickupItemEvent summoned by " + player.getName() + " for " + item.getType() + ".");

            setPlayerQuestProgression(event, player, event.getItem().getItemStack().getAmount(), "PICKUP");
        }
    }
}

