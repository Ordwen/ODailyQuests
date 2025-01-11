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

import java.util.HashMap;
import java.util.Map;

public class GetQuestChecker {

    /**
     * Validate GET quest type.
     *
     * @param player      player who is getting the item.
     * @param progression progression of the quest.
     * @param quest       quest to validate.
     */
    public static void makeQuestProgress(Player player, Progression progression, ItemQuest quest) {
        final PlayerInventory inventory = player.getInventory();
        int totalAmount = calculateTotalAmount(inventory, quest);

        if (totalAmount == -1) {
            // Dupe exploit detected
            sendMessage(player, QuestsMessages.CANNOT_COMPLETE_QUEST_WITH_OFF_HAND);
            return;
        }

        if (totalAmount >= quest.getAmountRequired()) {
            if (TakeItems.isTakeItemsEnabled()) {
                boolean success = removeRequiredItems(inventory, quest, quest.getAmountRequired());
                if (!success) {
                    sendMessage(player, QuestsMessages.NOT_ENOUGH_ITEM);
                    return;
                }
            }

            // Quest completed
            Bukkit.getPluginManager().callEvent(new QuestCompletedEvent(player, progression, quest));
            player.closeInventory();
        } else {
            sendMessage(player, QuestsMessages.NOT_ENOUGH_ITEM);
        }
    }

    /**
     * Calculate the total amount of required items in the inventory.
     *
     * @param inventory the player's inventory.
     * @param quest     the quest to validate.
     * @return total amount of required items, or -1 if offhand exploit is detected.
     */
    private static int calculateTotalAmount(PlayerInventory inventory, ItemQuest quest) {
        int totalAmount = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;

            if (quest.isRequiredItem(item)) {
                if (inventory.getItemInOffHand().equals(item)) {
                    return -1; // Dupe exploit
                }
                totalAmount += item.getAmount();
            }
        }

        return totalAmount;
    }

    /**
     * Remove the required items from the player's inventory.
     *
     * @param inventory     the player's inventory.
     * @param quest         the quest containing required item validation.
     * @param amountToRemove the total amount to remove.
     * @return true if the removal was successful, false otherwise.
     */
    private static boolean removeRequiredItems(PlayerInventory inventory, ItemQuest quest, int amountToRemove) {
        Map<Integer, ItemStack> backup = new HashMap<>();
        int removedAmount = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || !quest.isRequiredItem(item)) continue;

            int removable = Math.min(item.getAmount(), amountToRemove - removedAmount);
            backup.put(i, item.clone()); // Backup the item

            if (removable == item.getAmount()) {
                inventory.setItem(i, null); // Remove the entire stack
            } else {
                item.setAmount(item.getAmount() - removable); // Reduce stack size
            }

            removedAmount += removable;
            if (removedAmount >= amountToRemove) break;
        }

        // Check if removal was successful
        if (removedAmount < amountToRemove) {
            // Restore inventory if removal fails
            backup.forEach(inventory::setItem);
            return false;
        }

        return true;
    }

    /**
     * Send a message to the player if the message is defined.
     *
     * @param player the player to send the message to.
     * @param message the message enum.
     */
    private static void sendMessage(Player player, QuestsMessages message) {
        String msg = message.getMessage(player);
        if (msg != null) player.sendMessage(msg);
    }
}
