package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.items.PlayerHead;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }
        final Player player = (Player) event.getWhoClicked();
        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            for (InterfaceInventory interfaceInventory : questSystem.getCategorizedInterfaces()) {
                if (event.getClickedInventory().equals(interfaceInventory.getInventory())) {
                    event.setCancelled(true);
                    final ItemMeta clickedItemMeta = clickedItem.getItemMeta();
                    if (!questSystem.getEmptyCaseItems().contains(clickedItem)
                            && event.getClick().isLeftClick()
                            && event.getSlot() < event.getView().getTopInventory().getSize()
                            && !event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {

                        if (!clickedItem.equals(PlayerHead.getPlayerHead((Player) player, questSystem))) {

                            int page = interfaceInventory.getPage();
                            if (clickedItemMeta != null) {
                                if (clickedItemMeta.getDisplayName().equals(InterfacesManager.getNextPageItemName())) {
                                    player.closeInventory();
                                    player.openInventory(InterfacesManager.getInterfaceNextPage(questSystem, interfaceInventory.getCategory(), page, player).getInventory());
                                }
                                if (clickedItemMeta.getDisplayName().equals(InterfacesManager.getPreviousPageItemName())) {
                                    player.closeInventory();
                                    player.openInventory(InterfacesManager.getInterfacePreviousPage(questSystem, interfaceInventory.getCategory(), page, player).getInventory());
                                }
                            }
                        }
                    }
                }
            }
        });

        /*
        final String inventoryName = event.getView().getTitle();
        final Player player = (Player) event.getWhoClicked();
        
        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        final ItemMeta clickedItemMeta = clickedItem.getItemMeta();

        if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
            event.setCancelled(true);

            if (!InterfacesManager.getEmptyCaseItems().contains(clickedItem)
                    && event.getClick().isLeftClick()
                    && event.getSlot() < event.getView().getTopInventory().getSize()
                    && !event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {

                if (!clickedItem.equals(PlayerHead.getPlayerHead((Player) player))) {

                    int page = Integer.parseInt(inventoryName.substring(inventoryName.length() - 1)) - 1;
                    if (clickedItemMeta != null) {
                        if (clickedItemMeta.getDisplayName().equals(InterfacesManager.getNextPageItemName())) {
                            player.closeInventory();
                            
                            if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfaceNextPage("global", page, player));
                            } 
                            
                            else if (inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfaceNextPage("easy", page, player));
                            }
                            
                            else if (inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfaceNextPage("medium", page, player));
                            } 
                            
                            else if (inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfaceNextPage("hard", page, player));
                            }
                        }
                        
                        if (clickedItemMeta.getDisplayName().equals(InterfacesManager.getPreviousPageItemName())) {
                            player.closeInventory();

                            if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfacePreviousPage("global", page, player));
                            }

                            else if (inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfacePreviousPage("easy", page, player));
                            }

                            else if (inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfacePreviousPage("medium", page, player));
                            }

                            else if (inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
                                player.openInventory(InterfacesManager.getInterfacePreviousPage("hard", page, player));
                            }
                        }
                    }
                }
            }
        }

         */
    }
}
