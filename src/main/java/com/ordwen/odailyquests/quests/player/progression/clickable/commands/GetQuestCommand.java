package com.ordwen.odailyquests.quests.player.progression.clickable.commands;

import com.ordwen.odailyquests.configuration.functionalities.TakeItem;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestCommand;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestContext;
import com.ordwen.odailyquests.quests.types.inventory.GetQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;

public class GetQuestCommand extends QuestCommand<GetQuest> {

    public GetQuestCommand(QuestContext context, Progression progression, GetQuest quest) {
        super(context, progression, quest);
    }

    /**
     * Validate GET quest type.
     */
    @Override
    public void execute() {
        if (!quest.isAllowedToProgress(context.getPlayer(), quest)) return;

        final PlayerInventory inventory = context.getPlayer().getInventory();
        int totalAmount = calculateTotalAmount(inventory, quest);

        if (totalAmount == -1) {
            // Dupe exploit detected
            sendMessage(QuestsMessages.CANNOT_COMPLETE_QUEST_WITH_OFF_HAND);
            return;
        }

        if (totalAmount >= progression.getRequiredAmount()) {
            if (TakeItem.isTakeItemsEnabled()) {
                boolean success = removeRequiredItems(inventory, quest, progression.getRequiredAmount());
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

            if (quest.isRequiredItem(item, progression)) {
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
            final ItemStack item = inventory.getItem(i);
            if (item == null || !quest.isRequiredItem(item, progression)) continue;

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
