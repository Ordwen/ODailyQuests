package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.configuration.functionalities.TakeItems;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class GetQuestChecker {

    /**
     * Validate GET quest type.
     *
     * @param player      player who is getting the item.
     * @param progression progression of the quest.
     * @param quest       quest to validate.
     */
    public static void makeQuestProgress(Player player, Progression progression, ItemQuest quest) {
        final boolean ignoreNbt = quest.isIgnoreNbt();
        boolean hasRequiredAmount = false;
        int amount = 0;

        for (ItemStack item : quest.getRequiredItems()) {
            /* TEMPORARY FIX TO DUPE EXPLOIT */
            int result = getAmount(player.getInventory(), item, ignoreNbt);
            if (result == -1) {
                final String msg = QuestsMessages.CANNOT_COMPLETE_QUEST_WITH_OFF_HAND.getMessage(player);
                if (msg != null) player.sendMessage(msg);

                return;
            }

            amount += result;
            /* END OF TEMPORARY FIX */
        }

        if (amount >= quest.getAmountRequired()) {
            hasRequiredAmount = true;
        }

        if (hasRequiredAmount) {
            if (TakeItems.isTakeItemsEnabled()) {
                int totalRemoved = 0;
                for (ItemStack item : quest.getRequiredItems()) {
                    if (totalRemoved > quest.getAmountRequired()) break;

                    final ItemStack toRemove = item.clone();

                    int current = getAmount(player.getInventory(), item, ignoreNbt);
                    int removeAmount = Math.min(current, quest.getAmountRequired() - totalRemoved);

                    toRemove.setAmount(removeAmount);
                    if (!ignoreNbt) player.getInventory().removeItem(toRemove);
                    else removeItem(player.getInventory(), toRemove, removeAmount);

                    totalRemoved += current;
                }
            }

            final QuestCompletedEvent event = new QuestCompletedEvent(player, progression, quest);
            Bukkit.getPluginManager().callEvent(event);

            player.closeInventory();
        } else {
            final String msg = QuestsMessages.NOT_ENOUGH_ITEM.getMessage(player);
            if (msg != null) player.sendMessage(msg);
        }
    }

    /**
     * Remove item from player inventory by checking its type and amount, but ignoring NBT.
     *
     * @param inventory    player inventory to remove item from.
     * @param toRemove     item to remove.
     * @param removeAmount amount to remove.
     */
    private static void removeItem(PlayerInventory inventory, ItemStack toRemove, int removeAmount) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;

            if (item.getType() == toRemove.getType()) {
                if (item.getAmount() > removeAmount) {
                    item.setAmount(item.getAmount() - removeAmount);
                    break;
                } else {
                    removeAmount -= item.getAmount();
                    inventory.setItem(i, null);
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
    private static int getAmount(PlayerInventory playerInventory, ItemStack item, boolean ignoreNbt) {
        int amount = 0;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack == null) continue;

            if (ignoreNbt && item.getType() == itemStack.getType()) {
                amount += itemStack.getAmount();
                continue;
            }

            if (itemStack.isSimilar(item)) {

                // check if item have CustomModelData
                if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData()) {
                        if (itemStack.getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()) {

                            /* TEMPORARY FIX TO DUPE EXPLOIT */
                            if (playerInventory.getItemInOffHand().equals(itemStack)) {
                                return -1;
                            }
                            /* END OF TEMPORARY FIX */

                            amount += itemStack.getAmount();
                        }
                    }
                } else {
                    /* TEMPORARY FIX TO DUPE EXPLOIT */
                    if (playerInventory.getItemInOffHand().equals(itemStack)) {
                        return -1;
                    }
                    /* END OF TEMPORARY FIX */

                    amount += itemStack.getAmount();
                }
            }
        }

        return amount;
    }
}
