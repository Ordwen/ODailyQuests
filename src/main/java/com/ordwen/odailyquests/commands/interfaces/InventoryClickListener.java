package com.ordwen.odailyquests.commands.interfaces;

import com.ordwen.odailyquests.commands.interfaces.pagination.Items;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerHead;
import com.ordwen.odailyquests.commands.interfaces.playerinterface.PlayerQuestsInterface;
import com.ordwen.odailyquests.configuration.quests.player.progression.ProgressionManager;
import com.ordwen.odailyquests.configuration.quests.player.progression.ValidateVillagerTradeQuest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.MerchantInventory;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // check if player is trading
        if (event.getClickedInventory() != null
                && event.getInventory().getType() == InventoryType.MERCHANT
                && event.getSlotType() == InventoryType.SlotType.RESULT
                && event.getCurrentItem() != null
                && event.getCurrentItem().getType() != Material.AIR) {
            MerchantInventory merchantInventory = (MerchantInventory) event.getClickedInventory();
            if (event.getClickedInventory().getHolder() instanceof Villager villager) {
                if (merchantInventory.getSelectedRecipe() != null) {
                    ValidateVillagerTradeQuest.validateTradeQuestType(
                            event.getWhoClicked().getName(),
                            villager,
                            merchantInventory.getSelectedRecipe(),
                            event.getCurrentItem().getAmount());
                }
            }
            return;
        }

        String inventoryName = event.getView().getTitle();

        if (inventoryName.startsWith(InterfacesManager.getPlayerQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())
                || inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
            event.setCancelled(true);

            if (event.getCurrentItem() != null
                    && !InterfacesManager.getEmptyCaseItems().contains(event.getCurrentItem())
                    && event.getClick().isLeftClick()
                    && event.getSlot() < event.getView().getTopInventory().getSize()
                    && !event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {
                if (!Items.getPaginationItems().contains(event.getCurrentItem())
                        && inventoryName.startsWith(InterfacesManager.getPlayerQuestsInventoryName())) {

                    if (PlayerQuestsInterface.getFillItems().contains(event.getCurrentItem())) return;
                    if (PlayerQuestsInterface.getCloseItems().contains(event.getCurrentItem())) {
                        event.getWhoClicked().closeInventory();
                        return;
                    }

                    if (PlayerQuestsInterface.getConsoleCommandsItems().containsKey(event.getCurrentItem())) {
                        for (String cmd : PlayerQuestsInterface.getConsoleCommandsItems().get(event.getCurrentItem())) {
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", event.getWhoClicked().getName()));
                        }
                        return;
                    }

                    if (PlayerQuestsInterface.getPlayerCommandsItems().containsKey(event.getCurrentItem())) {
                        for (String cmd : PlayerQuestsInterface.getPlayerCommandsItems().get(event.getCurrentItem())) {
                            Bukkit.getServer().dispatchCommand(event.getWhoClicked(), cmd);
                        }
                        return;
                    }

                    ProgressionManager.validateGetQuestType(event.getWhoClicked().getName(), event.getCurrentItem());

                } else if (!event.getCurrentItem().equals(PlayerHead.getPlayerHead((Player) event.getWhoClicked()))) {
                    int page = Integer.parseInt(inventoryName.substring(inventoryName.length() - 1));
                    if (event.getCurrentItem().getItemMeta() != null) {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(InterfacesManager.getNextPageItemName())) {
                            event.getWhoClicked().closeInventory();
                            if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getGlobalQuestsInterface().getGlobalQuestsNextPage(page));
                            }
                            else if (inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfaceNextPage(InterfacesManager.getCategorizedQuestsInterfaces().getEasyQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfaceNextPage(InterfacesManager.getCategorizedQuestsInterfaces().getMediumQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfaceNextPage(InterfacesManager.getCategorizedQuestsInterfaces().getHardQuestsInventories(), page));
                            }
                        }
                        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(InterfacesManager.getPreviousPageItemName())) {
                            event.getWhoClicked().closeInventory();
                            if (inventoryName.startsWith(InterfacesManager.getGlobalQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getGlobalQuestsInterface().getGlobalQuestsPreviousPage(page));
                            }
                            else if (inventoryName.startsWith(InterfacesManager.getEasyQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfacePreviousPage(InterfacesManager.getCategorizedQuestsInterfaces().getEasyQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(InterfacesManager.getMediumQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfacePreviousPage(InterfacesManager.getCategorizedQuestsInterfaces().getMediumQuestsInventories(), page));
                            }
                            else if (inventoryName.startsWith(InterfacesManager.getHardQuestsInventoryName())) {
                                event.getWhoClicked().openInventory(InterfacesManager.getCategorizedQuestsInterfaces().getInterfacePreviousPage(InterfacesManager.getCategorizedQuestsInterfaces().getHardQuestsInventories(), page));
                            }
                        }
                    }
                }
            }
        }
    }
}
