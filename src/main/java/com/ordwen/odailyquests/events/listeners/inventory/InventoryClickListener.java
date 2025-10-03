package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.events.customs.CustomFurnaceExtractEvent;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.clickable.ClickableChecker;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.util.List;

public class InventoryClickListener extends ClickableChecker implements Listener {

    private final PlayerQuestsInterface playerQuestsInterface;

    public InventoryClickListener(PlayerQuestsInterface playerQuestsInterface) {
        this.playerQuestsInterface = playerQuestsInterface;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        final InventoryAction action = event.getAction();
        if (action == InventoryAction.NOTHING) return;

        final Player player = (Player) event.getWhoClicked();
        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
            return;
        }

        boolean isPlayerInterface = false;
        final String inventoryName = event.getView().getTitle();
        if (inventoryName.startsWith(playerQuestsInterface.getInterfaceName(player))) {
            isPlayerInterface = true;
            event.setCancelled(true);
        }

        final ItemStack clickedItem = event.getCurrentItem();
        Debugger.write("Clicked item: " + (clickedItem != null ? clickedItem.getType().name() : "null"));
        Debugger.write("Clicked item amount: " + (clickedItem != null ? clickedItem.getAmount() : "null"));
        Debugger.write("Cursor item: " + (event.getCursor() != null ? event.getCursor().getType().name() : "null"));
        Debugger.write("Cursor item amount: " + (event.getCursor() != null ? event.getCursor().getAmount() : "null"));

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (handleCustomFurnaceResult(event, action, clickedItem, player)) return;

        final QuestContext.Builder contextBuilder = new QuestContext.Builder(player).clickedItem(clickedItem);
        if (handleVillagerTrading(event, clickedItem, contextBuilder)) {
            return;
        }

        // do action related to the clicked item
        if (isPlayerInterface) {
            if (handlePlayerInterfaceClick(event, clickedItem, player)) return;
            processQuestCompletion(contextBuilder.build());
        }
    }

    /**
     * Handle player interface click events.
     *
     * @param event       the inventory click event.
     * @param clickedItem the clicked item.
     * @param player      the player who clicked the item.
     * @return true if the event is handled, false otherwise.
     */
    private boolean handlePlayerInterfaceClick(InventoryClickEvent event, ItemStack clickedItem, Player player) {
        if (event.getAction() == InventoryAction.HOTBAR_SWAP) return true;
        if (playerQuestsInterface.isFillItem(clickedItem)) return true;

        final int slot = event.getRawSlot();
        if (handlePlayerCommandItem(player, slot)) return true;
        if (handleConsoleCommandItem(player, slot)) return true;
        return handleCloseItem(clickedItem, player);
    }

    /**
     * If the clicked item is a villager trade result, handle the trade event.
     *
     * @param event          the inventory click event.
     * @param clickedItem    the clicked item.
     * @param contextBuilder the quest context builder.
     * @return true if the item is a villager trade result, false otherwise.
     */
    private boolean handleVillagerTrading(InventoryClickEvent event, ItemStack clickedItem, QuestContext.Builder contextBuilder) {
        if (event.getInventory().getType() == InventoryType.MERCHANT && event.getSlotType() == InventoryType.SlotType.RESULT) {
            Debugger.write("Detected villager trade action");
            final MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (merchantInventory == null) {
                Debugger.write("Merchant Inventory is null");
                return false;
            }

            if (event.getClickedInventory().getHolder() instanceof Villager villager) {
                int amount = getTradeAmount(event, clickedItem, merchantInventory);
                Debugger.write("Trade amount is " + amount);
                if (amount == 0) {
                    return true;
                }

                final MerchantRecipe selectedRecipe = merchantInventory.getSelectedRecipe();
                if (selectedRecipe != null) {
                    contextBuilder.villagerTrade(villager, selectedRecipe, amount);
                    processQuestCompletion(contextBuilder.build());
                }
            }

            return true;
        }
        return false;
    }

    /**
     * If the clicked item is a custom furnace result, handle the extraction event.
     * The associated configuration must be enabled.
     *
     * @param event       the inventory click event.
     * @param action      the inventory action.
     * @param clickedItem the clicked item.
     * @param player      the player who clicked the item.
     * @return true if the item is a custom furnace result, false otherwise.
     */
    private boolean handleCustomFurnaceResult(InventoryClickEvent event, InventoryAction action, ItemStack clickedItem, Player player) {
        if (CustomFurnaceResults.isEnabled()) {

            final InventoryType inventoryType = event.getInventory().getType();

            if (inventoryType == InventoryType.FURNACE
                    || inventoryType == InventoryType.BLAST_FURNACE
                    || inventoryType == InventoryType.SMOKER) {

                if (event.getSlotType() != InventoryType.SlotType.RESULT) return true;

                int amount;
                switch (action) {
                    case PICKUP_HALF -> amount = (int) Math.ceil(clickedItem.getAmount() / 2.0);
                    case PICKUP_ONE, DROP_ONE_SLOT -> amount = 1;
                    case MOVE_TO_OTHER_INVENTORY -> {
                        int max = clickedItem.getAmount();
                        amount = Math.min(max, fits(clickedItem, player.getInventory().getStorageContents()));
                    }
                    default -> amount = clickedItem.getAmount();
                }

                if (amount == 0) return true;

                final CustomFurnaceExtractEvent customFurnaceExtractEvent = new CustomFurnaceExtractEvent(player, clickedItem, amount);
                Bukkit.getServer().getPluginManager().callEvent(customFurnaceExtractEvent);

                return true;
            }
        }
        return false;
    }

    /**
     * Calculate the number of traded items from the villager trade result slot.
     * Takes into account single click vs bulk actions (Shift-click, Ctrl-click, hotbar move).
     *
     * @param event             the inventory click event
     * @param clickedItem       the clicked item in the result slot
     * @param merchantInventory the merchant inventory
     * @return the total number of result items the player will actually receive
     */
    private int getTradeAmount(InventoryClickEvent event, ItemStack clickedItem, MerchantInventory merchantInventory) {
        int perTradeResult = clickedItem.getAmount();
        Debugger.write("Per trade result amount is " + perTradeResult);
        if (perTradeResult <= 0) return 0;

        final ClickType click = event.getClick();
        Debugger.write("Click type is " + click.name());
        final InventoryAction action = event.getAction();
        Debugger.write("Inventory action is " + action.name());

        boolean bulk = (click == ClickType.SHIFT_LEFT || click == ClickType.SHIFT_RIGHT
                || action == InventoryAction.MOVE_TO_OTHER_INVENTORY
                || action == InventoryAction.HOTBAR_MOVE_AND_READD);

        Debugger.write("Bulk action is " + bulk);

        if (!bulk) {
            // simple click: only one trade
            return perTradeResult;
        }

        int tradesPossible = getMaxTradesPossible(merchantInventory);
        Debugger.write("Max trades possible is " + tradesPossible);
        if (tradesPossible <= 0) return 0;

        int capacityItems = fits(clickedItem, event.getView().getBottomInventory().getStorageContents());
        Debugger.write("Capacity in player inventory is " + capacityItems);
        if (capacityItems <= 0) return 0;

        int itemsIfUnlimitedSpace = tradesPossible * perTradeResult;
        Debugger.write("Items if unlimited space is " + itemsIfUnlimitedSpace);

        return Math.min(itemsIfUnlimitedSpace, capacityItems);
    }

    /**
     * Calculate the maximum number of trades that can be executed
     * with the current recipe, based on recipe uses left and the
     * available input ingredients in the merchant inventory.
     *
     * @param inv the merchant inventory
     * @return the maximum number of trades possible
     */
    private int getMaxTradesPossible(MerchantInventory inv) {
        final MerchantRecipe recipe = inv.getSelectedRecipe();
        if (recipe == null) return 0;

        int usesLeft = recipe.getMaxUses() - recipe.getUses();
        if (usesLeft <= 0) return 0;

        final List<ItemStack> reqs = recipe.getIngredients();
        final ItemStack req1 = !reqs.isEmpty() ? reqs.get(0) : null;
        final ItemStack req2 = reqs.size() >= 2 ? reqs.get(1) : null;

        final ItemStack in1 = inv.getItem(0);
        final ItemStack in2 = inv.getItem(1);

        int byIng1 = getIngredientCount(req1, in1, in2);
        int byIng2 = getIngredientCount(req2, in1, in2);

        int byIngredients = (byIng1 == Integer.MAX_VALUE && byIng2 == Integer.MAX_VALUE)
                ? usesLeft
                : Math.min(byIng1, byIng2);

        return Math.min(byIngredients, usesLeft);
    }

    /**
     * Calculate how many trades can be done with a specific required ingredient.
     * Checks both input slots and returns the number of times this ingredient
     * can satisfy the recipe requirement.
     *
     * @param required the required ingredient for the recipe
     * @param in1      the first input slot of the merchant inventory
     * @param in2      the second input slot of the merchant inventory
     * @return the maximum number of trades possible with this ingredient,
     *         or {@code Integer.MAX_VALUE} if the ingredient is not required
     */
    private int getIngredientCount(ItemStack required, ItemStack in1, ItemStack in2) {
        if (required == null || required.getType() == Material.AIR) return Integer.MAX_VALUE;

        int have = 0;
        if (in1 != null && in1.isSimilar(required)) have = in1.getAmount();
        else if (in2 != null && in2.isSimilar(required)) have = in2.getAmount();

        if (have <= 0) return 0;
        return have / Math.max(1, required.getAmount());
    }

    /**
     * Check if the clicked item is a close item. If so, close the player's inventory.
     *
     * @param clickedItem the clicked item.
     * @param player      the player who clicked the item.
     * @return true if the item is a close item, false otherwise.
     */
    private boolean handleCloseItem(ItemStack clickedItem, Player player) {
        if (playerQuestsInterface.isCloseItem(clickedItem)) {
            player.closeInventory();
            return true;
        }
        return false;
    }

    /**
     * Check if the clicked item is a player command item. If so, execute associated commands as player.
     *
     * @param player the player who clicked the item.
     * @param slot   the slot of the clicked item.
     * @return true if the item is a player command item, false otherwise.
     */
    private boolean handlePlayerCommandItem(Player player, int slot) {
        if (playerQuestsInterface.isPlayerCommandItem(slot)) {
            for (String cmd : playerQuestsInterface.getPlayerCommands(slot)) {
                Bukkit.getServer().dispatchCommand(player, cmd);
            }
            return true;
        }
        return false;
    }

    /**
     * Check if the clicked item is a console command item. If so, execute associated commands.
     *
     * @param player the player who clicked the item.
     * @param slot   the slot of the clicked item.
     * @return true if the item is a console command item, false otherwise.
     */
    private boolean handleConsoleCommandItem(Player player, int slot) {
        if (playerQuestsInterface.isConsoleCommandItem(slot)) {
            for (String cmd : playerQuestsInterface.getConsoleCommands(slot)) {
                ODailyQuests.morePaperLib.scheduling().globalRegionalScheduler().run(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName())));
            }
            return true;
        }
        return false;
    }
}
