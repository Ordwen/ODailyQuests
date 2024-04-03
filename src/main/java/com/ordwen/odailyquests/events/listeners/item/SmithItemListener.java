package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;

public class SmithItemListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onSmithItemEvent(SmithItemEvent event) {

        if (event.isCancelled()) return;
        if (event.getCurrentItem() == null) return;

        final ItemStack result = event.getCurrentItem();
        int recipeAmount = result.getAmount();

        final Player player = (Player) event.getWhoClicked();
        final ClickType click = event.getClick();

        if (movingItem(result, recipeAmount, player, click)) return;

        switch (click) {
            case NUMBER_KEY -> {
                if (event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null)
                    recipeAmount = 0;
            }
            case DROP, CONTROL_DROP -> {
                ItemStack cursor = event.getCursor();
                if (!(cursor == null || cursor.getType() == Material.AIR))
                    recipeAmount = 0;
            }
            case SHIFT_RIGHT, SHIFT_LEFT -> {
                if (recipeAmount == 0)
                    break;
                int maxCraftable = getMaxSmithAmount(event.getInventory());
                int capacity = fits(result, event.getView().getBottomInventory());
                if (capacity < maxCraftable)
                    maxCraftable = ((capacity + recipeAmount - 1) / recipeAmount) * recipeAmount;
                recipeAmount = maxCraftable;
            }
        }

        if (recipeAmount == 0)
            return;

        result.setAmount(recipeAmount);

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("SmithItemListener: onSmithItemEvent summoned by " + player.getName() + " for " + result.getType() + ".");
        setPlayerQuestProgression(player, result, result.getAmount(), "CRAFT");
    }
}
