package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.events.antiglitch.OpenedRecipes;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.MerchantRecipe;

public class PlayerInteractEntityListener implements Listener {

    /* Add recipes to the map when a villager is clicked */
    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getRightClicked() instanceof Villager villager) {
            for (MerchantRecipe recipe : villager.getRecipes()) {
                OpenedRecipes.put(recipe, recipe.getUses() - 1);
            }
        }
    }
}
