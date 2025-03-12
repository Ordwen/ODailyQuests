package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.commands.interfaces.holder.CategoryHolder;
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
        final Player player = (Player) event.getWhoClicked();

        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        final ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        if (clickedItemMeta == null) return;

        if (!(event.getView().getTopInventory().getHolder() instanceof CategoryHolder holder)) {
            return;
        }

        final String category = holder.category();
        final int page = holder.page();

        event.setCancelled(true);

        if (questsInterfaces.isEmptyCaseItem(clickedItem)
                || event.getClick().isShiftClick()
                || event.getSlot() >= event.getView().getTopInventory().getSize()
                || event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {
            return;
        }

        if (clickedItemMeta.getDisplayName().equals(questsInterfaces.getNextPageItemName())) {
            player.closeInventory();
            player.openInventory(questsInterfaces.getInterfaceNextPage(category, page, player));
        } else if (clickedItemMeta.getDisplayName().equals(questsInterfaces.getPreviousPageItemName())) {
            player.closeInventory();
            player.openInventory(questsInterfaces.getInterfacePreviousPage(category, page, player));
        }
    }
}
