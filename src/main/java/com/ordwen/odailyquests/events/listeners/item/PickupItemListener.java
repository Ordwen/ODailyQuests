package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PickupItemListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onPickupItemEvent(EntityPickupItemEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {

            final ItemStack item = event.getItem().getItemStack();

            if (Antiglitch.isStoreDroppedItems()) {
                if (item.hasItemMeta()) {
                    final PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
                    final String droppedKey = pdc.get(Antiglitch.DROPPED_BY, PersistentDataType.STRING);

                    if (droppedKey != null && droppedKey.equals(player.getUniqueId().toString())) {
                        return;
                    }
                }
            }

            setPlayerQuestProgression(player, item, event.getItem().getItemStack().getAmount(), QuestType.PICKUP, event.getItem().getUniqueId().toString());
        }
    }
}

