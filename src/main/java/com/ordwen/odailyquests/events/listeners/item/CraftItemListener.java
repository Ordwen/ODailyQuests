package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.*;

public class CraftItemListener extends PlayerProgressor implements Listener {

    /**
     * Handles crafting events and updates quest progression if the crafted
     * items are valid and effectively added to the player's inventory.
     *
     * @param event the craft item event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItemEvent(CraftItemEvent event) {
        if (event.isCancelled()) return;

        final ItemStack current = event.getCurrentItem();
        if (current == null) return;

        final Player player = (Player) event.getWhoClicked();
        final ItemStack result = resolveResultItem(event, current);
        if (result == null) return;

        final int amount = computeRecipeAmount(event, player, result);
        if (amount <= 0) return;

        result.setAmount(amount);

        Debugger.write("CraftItemListener: onCraftItemEvent summoned by " + player.getName() + " for " + result.getType() + " x" + result.getAmount() + ".");
        setPlayerQuestProgression(event, player, result.getAmount(), "CRAFT");
    }

    /**
     * Resolves the resulting item of the crafting recipe.
     * Handles both standard recipes and {@link ComplexRecipe}.
     *
     * @param event    the craft item event
     * @param fallback the fallback item if the recipe is not complex
     * @return the resulting item stack, or {@code null} if invalid
     */
    private ItemStack resolveResultItem(CraftItemEvent event, ItemStack fallback) {
        if (event.getRecipe() instanceof ComplexRecipe complex) {
            final Material mat = Material.getMaterial(complex.getKey().getKey().toUpperCase());
            return (mat == null) ? null : new ItemStack(mat);
        }
        return fallback.clone();
    }

    /**
     * Computes the number of items that should be crafted depending
     * on the click type and the player's inventory state.
     *
     * @param event   the craft item event
     * @param player  the player crafting the item
     * @param sample  a sample of the resulting item
     * @return the number of items that will be crafted
     */
    private int computeRecipeAmount(CraftItemEvent event, Player player, ItemStack sample) {
        final ClickType click = event.getClick();
        final int baseAmount = sample.getAmount();

        return switch (click) {
            case NUMBER_KEY -> handleNumberKey(event, baseAmount);
            case DROP, CONTROL_DROP -> handleDrop(event, baseAmount);
            case LEFT, RIGHT -> handleCursorClick(event, sample, baseAmount, click == ClickType.RIGHT);
            case SHIFT_LEFT, SHIFT_RIGHT -> handleShiftClick(event, player, sample, baseAmount);
            case SWAP_OFFHAND -> handleSwapOffhand(player);
            default -> 0;
        };
    }

    /**
     * Handles crafting when the player uses a number key to move the result
     * into their hotbar.
     *
     * @param event      the craft item event
     * @param baseAmount the default crafted amount
     * @return the final crafted amount
     */
    private int handleNumberKey(CraftItemEvent event, int baseAmount) {
        boolean slotBusy = event.getWhoClicked().getInventory().getItem(event.getHotbarButton()) != null;
        return slotBusy ? 0 : baseAmount;
    }

    /**
     * Handles crafting when the player tries to drop the result item directly.
     *
     * @param event      the craft item event
     * @param baseAmount the default crafted amount
     * @return the final crafted amount
     */
    private int handleDrop(CraftItemEvent event, int baseAmount) {
        return isCursorEmpty(event.getCursor()) ? baseAmount : 0;
    }

    /**
     * Handles crafting when the player left/right-clicks on the result slot.
     * Takes into account whether the cursor already holds items.
     *
     * @param event      the craft item event
     * @param sample     the resulting item
     * @param baseAmount the default crafted amount
     * @param rightClick true if right click, false if left click
     * @return the final crafted amount
     */
    private int handleCursorClick(CraftItemEvent event, ItemStack sample, int baseAmount, boolean rightClick) {
        final ItemStack cursor = event.getCursor();

        if (isCursorEmpty(cursor)) {
            return rightClick ? Math.min(1, baseAmount) : baseAmount;
        }

        if (!cursor.isSimilar(sample)) return 0;

        final int max = Math.min(cursor.getMaxStackSize(), sample.getMaxStackSize());
        final int space = max - cursor.getAmount();
        if (space <= 0) return 0;

        return rightClick ? 1 : Math.min(baseAmount, space);
    }

    /**
     * Handles crafting when the player shift-clicks the result to move it
     * directly into their inventory.
     *
     * @param event      the craft item event
     * @param player     the player
     * @param sample     the resulting item
     * @param baseAmount the default crafted amount
     * @return the maximum number of items that can be crafted and stored
     */
    private int handleShiftClick(CraftItemEvent event, Player player, ItemStack sample, int baseAmount) {
        if (baseAmount <= 0) return 0;

        int maxCraftable = getMaxCraftAmount(event.getInventory());
        int capacity = fits(sample, player.getInventory().getStorageContents());
        if (capacity < maxCraftable) {
            maxCraftable = ((capacity + baseAmount - 1) / baseAmount) * baseAmount;
        }
        return maxCraftable;
    }

    /**
     * Handles crafting when the player swaps the result item with their offhand.
     *
     * @param player the player
     * @return 1 if successful, 0 if the offhand is occupied
     */
    private int handleSwapOffhand(Player player) {
        boolean offhandBusy = player.getInventory().getItemInOffHand().getType() != Material.AIR;
        return offhandBusy ? 0 : 1;
    }

    /**
     * Checks if the cursor (mouse slot) is empty.
     *
     * @param cursor the current cursor item
     * @return true if empty, false otherwise
     */
    private boolean isCursorEmpty(ItemStack cursor) {
        return cursor == null || cursor.getType() == Material.AIR;
    }

    /**
     * Returns the maximum number of items that can be crafted based on the
     * recipe result amount and the lowest amount of input materials.
     *
     * @param inv the crafting inventory
     * @return the maximum craftable items
     */
    private int getMaxCraftAmount(CraftingInventory inv) {
        if (inv.getResult() == null) return 0;

        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getMatrix()) {
            if (is != null && is.getAmount() < materialCount) {
                materialCount = is.getAmount();
            }
        }

        return resultCount * materialCount;
    }
}
