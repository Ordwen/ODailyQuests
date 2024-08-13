package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

public class SmithItemListener extends PlayerProgressor implements Listener {

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
                int capacity = fits(result, player.getInventory().getStorageContents());
                if (capacity < maxCraftable) {
                    maxCraftable = ((capacity + recipeAmount - 1) / recipeAmount) * recipeAmount;
                }
                recipeAmount = maxCraftable;
            }
            case SWAP_OFFHAND -> {
                boolean haveItemInOffHand = player.getInventory().getItemInOffHand().getType() != Material.AIR;
                if (haveItemInOffHand) recipeAmount = 0;
                else recipeAmount = 1;
            }
        }

        if (recipeAmount == 0)
            return;

        result.setAmount(recipeAmount);

        Debugger.addDebug("=========================================================================================");
        Debugger.addDebug("SmithItemListener: onSmithItemEvent summoned by " + player.getName() + " for " + result.getType() + ".");
        setPlayerQuestProgression(event, player, result.getAmount(), "CRAFT");
    }

    /**
     * Returns the maximum amount of items that can be smithed in the given inventory.
     *
     * @param inv the inventory to check.
     * @return the maximum.
     */
    private int getMaxSmithAmount(SmithingInventory inv) {
        if (inv.getResult() == null) return 0;

        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getContents())
            if (is != null && is.getAmount() < materialCount) materialCount = is.getAmount();

        return resultCount * materialCount;
    }
}
