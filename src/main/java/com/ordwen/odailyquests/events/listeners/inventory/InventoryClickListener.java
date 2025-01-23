package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.UseCustomFurnaceResults;
import com.ordwen.odailyquests.events.customs.CustomFurnaceExtractEvent;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractClickableChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

public class InventoryClickListener extends AbstractClickableChecker implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
            return;
        }

        final ItemStack clickedItem = event.getCurrentItem();
        final InventoryAction action = event.getAction();
        if (action == InventoryAction.NOTHING) return;
        final int slot = event.getRawSlot();

        if (event.getClickedInventory() == null) return;
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        // check if player is trading
        if (event.getInventory().getType() == InventoryType.MERCHANT && event.getSlotType() == InventoryType.SlotType.RESULT) {

            final MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (event.getClickedInventory().getHolder() instanceof Villager villager) {

                int amount = clickedItem.getAmount();

                final ClickType click = event.getClick();
                switch (click) {
                    case SHIFT_RIGHT, SHIFT_LEFT -> {
                        if (clickedItem.getAmount() == 0) break;
                        int maxTradable = getMaxTradeAmount(merchantInventory);
                        int capacity = fits(clickedItem, event.getView().getBottomInventory().getStorageContents());
                        if (capacity < maxTradable) {
                            maxTradable = ((capacity + clickedItem.getAmount() - 1) / clickedItem.getAmount()) * clickedItem.getAmount();
                        }
                        amount = maxTradable;
                    }
                }

                if (amount == 0) return;

                if (merchantInventory.getSelectedRecipe() != null) {
                    validateTradeQuestType(
                            player,
                            villager,
                            merchantInventory.getSelectedRecipe(),
                            amount);
                }
            }

            return;
        }

        // check if player is extracting from furnace
        if (UseCustomFurnaceResults.isEnabled()) {

            final InventoryType inventoryType = event.getInventory().getType();

            if (inventoryType == InventoryType.FURNACE
                    || inventoryType == InventoryType.BLAST_FURNACE
                    || inventoryType == InventoryType.SMOKER) {

                if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

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

                if (amount == 0) return;

                final CustomFurnaceExtractEvent customFurnaceExtractEvent = new CustomFurnaceExtractEvent(player, clickedItem, amount);
                Bukkit.getServer().getPluginManager().callEvent(customFurnaceExtractEvent);

                return;
            }
        }

        // do action related to the clicked item
        final String inventoryName = event.getView().getTitle();
        if (inventoryName.startsWith(PlayerQuestsInterface.getInterfaceName(player))) {
            event.setCancelled(true);

            if (event.getAction() == InventoryAction.HOTBAR_SWAP) return;
            if (PlayerQuestsInterface.getFillItems().contains(clickedItem)) return;
            if (PlayerQuestsInterface.getCloseItems().contains(clickedItem)) {
                event.getWhoClicked().closeInventory();
                return;
            }

            if (PlayerQuestsInterface.getPlayerCommandsItems().containsKey(slot)) {
                for (String cmd : PlayerQuestsInterface.getPlayerCommandsItems().get(slot)) {
                    Bukkit.getServer().dispatchCommand(event.getWhoClicked(), cmd);
                }
                return;
            }

            if (PlayerQuestsInterface.getConsoleCommandsItems().containsKey(slot)) {
                for (String cmd : PlayerQuestsInterface.getConsoleCommandsItems().get(slot)) {
                    ODailyQuests.morePaperLib.scheduling().globalRegionalScheduler().run(() -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", event.getWhoClicked().getName())));
                }
                return;
            }

            // complete quest for types that requires a click in player interface (GET - LOCATION - PLACEHOLDER)
            setPlayerQuestProgression(player, clickedItem);
        }
    }

    private int getMaxTradeAmount(MerchantInventory inv) {
        if (inv.getSelectedRecipe() == null) return 0;

        int resultCount = inv.getSelectedRecipe().getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getStorageContents())
            if (is != null && is.getAmount() < materialCount) materialCount = is.getAmount();

        return resultCount * materialCount;
    }
}
