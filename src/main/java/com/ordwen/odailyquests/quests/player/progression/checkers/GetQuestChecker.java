package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.functionalities.TakeItems;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.inventory.GetQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class GetQuestChecker extends QuestChecker<GetQuest> {

    public GetQuestChecker(Player player, Progression progression, GetQuest quest) {
        super(player, progression, quest);
    }

    /**
     * Validate GET quest type.
     */
    @Override
    public void validateAndComplete() {
        if (!quest.isAllowedToProgress(player, quest)) return;

        final PlayerInventory inventory = player.getInventory();
        int totalAmount = calculateTotalAmount(inventory, quest);

        if (totalAmount == -1) {
            // Dupe exploit detected
            sendMessage(QuestsMessages.CANNOT_COMPLETE_QUEST_WITH_OFF_HAND);
            return;
        }

        if (totalAmount >= quest.getAmountRequired()) {
            if (TakeItems.isTakeItemsEnabled()) {
                boolean success = removeRequiredItems(inventory, quest, quest.getAmountRequired());
                if (!success) {
                    sendMessage(QuestsMessages.NOT_ENOUGH_ITEM);
                    return;
                }
            }

            completeQuest();
        } else {
            sendMessage(QuestsMessages.NOT_ENOUGH_ITEM);
        }
    }

    /**
     * Calculate the total amount of required items in the inventory.
     *
     * @param inventory the player's inventory.
     * @param quest     the quest to validate.
     * @return total amount of required items, or -1 if offhand exploit is detected.
     */
    private int calculateTotalAmount(PlayerInventory inventory, ItemQuest quest) {
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
    private boolean removeRequiredItems(PlayerInventory inventory, ItemQuest quest, int amountToRemove) {
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
}
