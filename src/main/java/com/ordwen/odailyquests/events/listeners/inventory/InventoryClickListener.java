package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.QuestInventory;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.essentials.UseCustomFurnaceResults;
import com.ordwen.odailyquests.events.customs.CustomFurnaceExtractEvent;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractClickableChecker;
import com.ordwen.odailyquests.tools.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;

import java.time.LocalDateTime;

public class InventoryClickListener extends AbstractClickableChecker implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        final ItemStack clickedItem = event.getCurrentItem();

        final InventoryAction action = event.getAction();
        if (action == InventoryAction.NOTHING) return;

        final int slot = event.getRawSlot();

        if (event.getClickedInventory() == null) return;
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player player = (Player) event.getWhoClicked();

        // check if player is trading
        if (event.getInventory().getType() == InventoryType.MERCHANT && event.getSlotType() == InventoryType.SlotType.RESULT) {

            final MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (event.getClickedInventory().getHolder() instanceof Villager villager) {

                if (merchantInventory.getSelectedRecipe() != null) {
                    ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
                        validateTradeQuestType(
                                questSystem,
                                player,
                                villager,
                                merchantInventory.getSelectedRecipe(),
                                clickedItem.getAmount());
                    });
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
                    case PICKUP_ONE -> amount = 1;
                    case MOVE_TO_OTHER_INVENTORY -> {
                        int max = clickedItem.getAmount();
                        amount = Math.min(max, fits(clickedItem, player.getInventory()));
                    }
                    default -> amount = clickedItem.getAmount();
                }

                if (amount == 0) return;

                final CustomFurnaceExtractEvent customFurnaceExtractEvent = new CustomFurnaceExtractEvent(player, clickedItem, amount);
                Bukkit.getServer().getPluginManager().callEvent(customFurnaceExtractEvent);

                return;
            }
        }

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {

            // do action related to the clicked item
            if (event.getClickedInventory().getHolder() instanceof QuestInventory) {
                event.setCancelled(true);

                if (event.getAction() == InventoryAction.HOTBAR_SWAP) return;

                if (questSystem.getFillItems().contains(clickedItem)) return;

                if (questSystem.getCloseItems().contains(clickedItem)) {
                    event.getWhoClicked().closeInventory();
                    return;
                }

                if (questSystem.getPlayerCommandsItems().containsKey(slot)) {
                    for (String cmd : questSystem.getPlayerCommandsItems().get(slot)) {
                        Bukkit.getServer().dispatchCommand(event.getWhoClicked(), cmd);
                    }
                    return;
                }

                if (questSystem.getConsoleCommandsItems().containsKey(slot)) {
                    for (String cmd : questSystem.getConsoleCommandsItems().get(slot)) {
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", event.getWhoClicked().getName()));
                    }
                    return;
                }

                // complete quest for types that requires a click ( GET - LOCATION - PLACEHOLDER)
                setPlayerQuestProgression(questSystem, player, clickedItem);

            }
        });
    }

    /**
     * @param expected
     * @param inv
     * @return
     */
    private int fits(ItemStack expected, Inventory inv) {
        int result = 0;

        for (ItemStack compared : inv.getStorageContents()) {
            if (compared == null) {
                result += expected.getMaxStackSize();
            } else if (compared.isSimilar(expected)) {
                result += Math.max(expected.getMaxStackSize() - compared.getAmount(), 0);
            }
        }

        return result;
    }
}
