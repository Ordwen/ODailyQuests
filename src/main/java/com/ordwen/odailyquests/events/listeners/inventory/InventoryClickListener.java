package com.ordwen.odailyquests.events.listeners.inventory;

import com.ordwen.odailyquests.commands.interfaces.InterfacesManager;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.TakeItems;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.events.antiglitch.OpenedRecipes;
import com.ordwen.odailyquests.quests.player.progression.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.types.ItemQuest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.types.VillagerQuest;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {

        if (event.getClickedInventory() == null) return;

        final ItemStack clickedItem = event.getCurrentItem();
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
                            event.getCurrentItem().getAmount());
                }
            }
            return;
        }

        // complete quest get type
        final String inventoryName = event.getView().getTitle();
        if (inventoryName.startsWith(InterfacesManager.getPlayerQuestsInventoryName())) {
            event.setCancelled(true);

            validateGetQuestType(player, clickedItem);
        }
    }

    /**
     * Check if player can validate a quest with type GET.
     *
     * @param player      player to check.
     * @param clickedItem item clicked in the inventory.
     */
    public void validateGetQuestType(Player player, ItemStack clickedItem) {

        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            return;
        }

        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {

            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();
            for (AbstractQuest abstractQuest : playerQuests.keySet()) {

                if (abstractQuest instanceof ItemQuest quest) {
                    if (clickedItem.equals(quest.getMenuItem()) && quest.getType() == QuestType.GET) {

                        final Progression questProgression = playerQuests.get(quest);
                        if (!questProgression.isAchieved()) {

                            if (getAmount(player.getInventory(), quest.getRequiredItem()) >= quest.getAmountRequired()) {
                                questProgression.setAchieved();
                                QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());

                                if (TakeItems.isTakeItemsEnabled()) {
                                    final ItemStack toRemove = quest.getRequiredItem().clone();
                                    toRemove.setAmount(quest.getAmountRequired());
                                    player.getInventory().removeItem(toRemove);
                                }

                                player.closeInventory();
                                RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());
                            } else player.sendMessage(QuestsMessages.NOT_ENOUGH_ITEM.toString());
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * Count amount of an item in player inventory.
     *
     * @param playerInventory player inventory to check.
     * @param item            material to check.
     * @return amount of material.
     */
    private int getAmount(PlayerInventory playerInventory, ItemStack item) {
        int amount = 0;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack != null && itemStack.isSimilar(item)) amount += itemStack.getAmount();
        }
        return amount;
    }

    /**
     * Validate VILLAGER_TRADE quest type.
     *
     * @param player     player who is trading.
     * @param villager       villager wich one the player is trading.
     * @param selectedRecipe item trade.
     * @param quantity       quantity trade.
     */
    public void validateTradeQuestType(Player player, Villager villager, MerchantRecipe selectedRecipe, int quantity) {
        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {

            HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();

            for (AbstractQuest abstractQuest : playerQuests.keySet()) {
                if (abstractQuest instanceof VillagerQuest quest) {
                    boolean valid = false;
                    Progression questProgression = playerQuests.get(quest);
                    if (!questProgression.isAchieved() && quest.getType() == QuestType.VILLAGER_TRADE) {

                        if (quest.getRequiredItem() == null) valid = true;
                        else if (quest.getRequiredItem().getType() == selectedRecipe.getResult().getType())
                            valid = true;

                        if (selectedRecipe.getUses() <= OpenedRecipes.get(selectedRecipe)) {
                            valid = false;
                        }

                        if (valid) {
                            if (villager != null) {
                                if (quest.getVillagerProfession() != null) {
                                    if (!quest.getVillagerProfession().equals(villager.getProfession())) {
                                        valid = false;
                                    }
                                }
                                if (quest.getVillagerLevel() != 0) {
                                    if (!(quest.getVillagerLevel() == villager.getVillagerLevel())) {
                                        valid = false;
                                    }
                                }
                            }
                        }

                        if (valid) {
                            for (int i = 0; i < quantity; i++) {
                                questProgression.increaseProgression();
                            }
                            if (questProgression.getProgression() >= quest.getAmountRequired()) {
                                questProgression.setAchieved();
                                QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());
                                RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());
                            }

                            if (!Synchronization.isSynchronised()) break;
                        }
                    }
                }
            }
        }
    }
}
