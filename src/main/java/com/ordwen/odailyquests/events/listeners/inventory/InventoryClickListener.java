package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractSpecifiedChecker;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

public class InventoryClickListener extends AbstractSpecifiedChecker implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        final ItemStack clickedItem = event.getCurrentItem();
        final InventoryAction action = event.getAction();

        final int slot = event.getRawSlot();

        if (event.getClickedInventory() == null) return;
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player player = (Player) event.getWhoClicked();

        // check if player is trading
        if (event.getInventory().getType() == InventoryType.MERCHANT && event.getSlotType() == InventoryType.SlotType.RESULT) {

            final MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (event.getClickedInventory().getHolder() instanceof Villager villager) {

                if (merchantInventory.getSelectedRecipe() != null) {
                    validateTradeQuestType(
                            player,
                            villager,
                            merchantInventory.getSelectedRecipe(),
                            clickedItem.getAmount());
                }
            }
            return;
        }

        // do action related to the clicked item
        final String inventoryName = event.getView().getTitle();
        if (inventoryName.startsWith(InterfacesManager.getPlayerQuestsInventoryName())) {
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
            }

            if (PlayerQuestsInterface.getConsoleCommandsItems().containsKey(slot)) {
                for (String cmd : PlayerQuestsInterface.getConsoleCommandsItems().get(slot)) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", event.getWhoClicked().getName()));
                }
                return;
            }

            // complete quest for types that requires a click ( GET - LOCATION - PLACEHOLDER)
            setPlayerQuestProgression(player, clickedItem);
        }
    }
}
