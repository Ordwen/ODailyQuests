package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;

public class CraftItemListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onCraftItemEvent(CraftItemEvent event) {
        if (event.isCancelled()) return;
        if (event.getCurrentItem() == null) return;

        ItemStack test;
        final Player player = (Player) event.getWhoClicked();

        if (event.getRecipe() instanceof ComplexRecipe complexRecipe) {
            final Material material = Material.getMaterial(complexRecipe.getKey().getKey().toUpperCase());
            if (material == null) return;
            test = new ItemStack(material);
        } else {
            test = event.getCurrentItem().clone();
        }

        final ClickType click = event.getClick();
        int recipeAmount = test.getAmount();

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
                int maxCraftable = getMaxCraftAmount(event.getInventory());
                int capacity = fits(test, player.getInventory().getStorageContents());
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

        if (recipeAmount == 0) return;

        test.setAmount(recipeAmount);

        Debugger.addDebug("CraftItemListener: onCraftItemEvent summoned by " + player.getName() + " for " + test.getType() + " x" + test.getAmount() + ".");
        setPlayerQuestProgression(event, player, test.getAmount(), "CRAFT");
    }

    /**
     * Returns the maximum amount of items that can be crafted in the given inventory.
     *
     * @param inv the inventory to check.
     * @return the maximum.
     */
    private int getMaxCraftAmount(CraftingInventory inv) {
        if (inv.getResult() == null) return 0;

        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getMatrix())
            if (is != null && is.getAmount() < materialCount) materialCount = is.getAmount();

        return resultCount * materialCount;
    }
}
