package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerHead;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        final String inventoryName = event.getView().getTitle();

        if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null
                    && !InterfacesManager.getEmptyCaseItems().contains(event.getCurrentItem())
                    && event.getClick().isLeftClick()
                    && event.getSlot() < event.getView().getTopInventory().getSize()
                    && !event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {

                if (!event.getCurrentItem().equals(PlayerHead.getPlayerHead((Player) event.getWhoClicked()))) {

                    int page = Integer.parseInt(inventoryName.substring(inventoryName.length() - 1));
                    if (event.getCurrentItem().getItemMeta() != null) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(InterfacesManager.getNextPageItemName())) {
                            event.getWhoClicked().closeInventory();
                            if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getGlobalQuestsInterface().getGlobalQuestsNextPage(page));
                            } else if (inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfaceNextPage(InterfacesManager.getCategorizedQuestsInterfaces().getEasyQuestsInventories(), page));
                            } else if (inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfaceNextPage(InterfacesManager.getCategorizedQuestsInterfaces().getMediumQuestsInventories(), page));
                            } else if (inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfaceNextPage(InterfacesManager.getCategorizedQuestsInterfaces().getHardQuestsInventories(), page));
                            }
                        }
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(InterfacesManager.getPreviousPageItemName())) {
                            event.getWhoClicked().closeInventory();
                            if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getGlobalQuestsInterface().getGlobalQuestsPreviousPage(page));
                            } else if (inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfacePreviousPage(InterfacesManager.getCategorizedQuestsInterfaces().getEasyQuestsInventories(), page));
                            } else if (inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfacePreviousPage(InterfacesManager.getCategorizedQuestsInterfaces().getMediumQuestsInventories(), page));
                            } else if (inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfacePreviousPage(InterfacesManager.getCategorizedQuestsInterfaces().getHardQuestsInventories(), page));
                            }
                        }
                    }
                }
            }
        }
    }
}
