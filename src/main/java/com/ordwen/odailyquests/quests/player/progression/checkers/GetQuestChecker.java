package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.configuration.functionalities.TakeItems;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.ItemQuest;
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
        boolean hasRequiredAmount = false;
        int amount = 0;

        for (ItemStack item : quest.getRequiredItems()) {
            /* TEMPORARY FIX TO DUPE EXPLOIT */
            int result = getAmount(player.getInventory(), item);
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

                    int current = getAmount(player.getInventory(), item);
                    int removeAmount = Math.min(current, quest.getAmountRequired() - totalRemoved);

                    toRemove.setAmount(removeAmount);
                    player.getInventory().removeItem(toRemove);

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
     * Count amount of an item in player inventory.
     *
     * @param playerInventory player inventory to check.
     * @param item            material to check.
     * @return amount of material.
     */
    private static int getAmount(PlayerInventory playerInventory, ItemStack item) {
        int amount = 0;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack != null && itemStack.isSimilar(item)) {

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
