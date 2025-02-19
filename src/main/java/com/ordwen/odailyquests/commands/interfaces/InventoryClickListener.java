package com.ordwen.odailyquests.commands.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryClickListener implements Listener {

    private final QuestsInterfaces questsInterfaces;

    public InventoryClickListener(QuestsInterfaces questsInterfaces) {
        this.questsInterfaces = questsInterfaces;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        final String inventoryName = event.getView().getTitle();
        final Player player = (Player) event.getWhoClicked();

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        final ItemMeta clickedItemMeta = clickedItem.getItemMeta();

        if (inventoryName.startsWith(questsInterfaces.getGlobalQuestsInventoryName())
                || inventoryName.startsWith(questsInterfaces.getEasyQuestsInventoryName())
                || inventoryName.startsWith(questsInterfaces.getMediumQuestsInventoryName())
                || inventoryName.startsWith(questsInterfaces.getHardQuestsInventoryName())) {
            event.setCancelled(true);

            if (!questsInterfaces.isEmptyCaseItem(clickedItem)
                    && event.getClick().isLeftClick()
                    && event.getSlot() < event.getView().getTopInventory().getSize()
                    && !event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {

                int page = Integer.parseInt(inventoryName.substring(inventoryName.length() - 1)) - 1;
                if (clickedItemMeta != null) {
                    if (clickedItemMeta.getDisplayName().equals(questsInterfaces.getNextPageItemName())) {
                        player.closeInventory();

                        if (inventoryName.startsWith(questsInterfaces.getGlobalQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfaceNextPage("global", page, player));
                        } else if (inventoryName.startsWith(questsInterfaces.getEasyQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfaceNextPage("easy", page, player));
                        } else if (inventoryName.startsWith(questsInterfaces.getMediumQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfaceNextPage("medium", page, player));
                        } else if (inventoryName.startsWith(questsInterfaces.getHardQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfaceNextPage("hard", page, player));
                        }
                    }

                    if (clickedItemMeta.getDisplayName().equals(questsInterfaces.getPreviousPageItemName())) {
                        player.closeInventory();

                        if (inventoryName.startsWith(questsInterfaces.getGlobalQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfacePreviousPage("global", page, player));
                        } else if (inventoryName.startsWith(questsInterfaces.getEasyQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfacePreviousPage("easy", page, player));
                        } else if (inventoryName.startsWith(questsInterfaces.getMediumQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfacePreviousPage("medium", page, player));
                        } else if (inventoryName.startsWith(questsInterfaces.getHardQuestsInventoryName())) {
                            player.openInventory(questsInterfaces.getInterfacePreviousPage("hard", page, player));
                        }
                    }
                }
            }
        }
    }
}
