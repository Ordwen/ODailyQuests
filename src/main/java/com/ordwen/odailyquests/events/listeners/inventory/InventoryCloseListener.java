package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.events.antiglitch.OpenedRecipes;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.MerchantRecipe;

public class InventoryCloseListener implements Listener {

    /* Remove recipes from the map when villager menu is closed */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT
                && event.getInventory().getHolder() instanceof Villager villager) {
            for (MerchantRecipe recipe : villager.getRecipes()) {
                OpenedRecipes.remove(recipe);
            }
        }
    }
}
