package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
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
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        if (handleCustomFurnaceResult(event, action, clickedItem, player)) return;

        final QuestContext.Builder contextBuilder = new QuestContext.Builder(player).clickedItem(clickedItem);
        if (handleVillagerTrading(event, clickedItem, contextBuilder)) return;

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

            final MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (merchantInventory == null) return false;

            if (event.getClickedInventory().getHolder() instanceof Villager villager) {

                int amount = getTradeAmount(event, clickedItem, merchantInventory);
                if (amount == 0) return true;

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
     * Get the amount of traded items based on the click type.
     *
     * @param event             the inventory click event.
     * @param clickedItem       the clicked item.
     * @param merchantInventory the merchant inventory.
     * @return the amount of traded items.
     */
    private int getTradeAmount(InventoryClickEvent event, ItemStack clickedItem, MerchantInventory merchantInventory) {
        int amount = clickedItem.getAmount();

        final ClickType click = event.getClick();
        if ((click == ClickType.SHIFT_RIGHT || click == ClickType.SHIFT_LEFT) && amount != 0) {
            int maxTradable = getMaxTradeAmount(merchantInventory);
            int capacity = fits(clickedItem, event.getView().getBottomInventory().getStorageContents());
            if (capacity < maxTradable) {
                maxTradable = ((capacity + amount - 1) / amount) * amount;
            }
            amount = maxTradable;
        }
        return amount;
    }

    /**
     * Get the maximum amount of items that can be traded in the current trade.
     *
     * @param inv the merchant inventory.
     * @return the maximum amount of items that can be traded.
     */
    private int getMaxTradeAmount(MerchantInventory inv) {
        if (inv.getSelectedRecipe() == null) return 0;

        int resultCount = inv.getSelectedRecipe().getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getStorageContents())
            if (is != null && is.getAmount() < materialCount) materialCount = is.getAmount();

        return resultCount * materialCount;
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
