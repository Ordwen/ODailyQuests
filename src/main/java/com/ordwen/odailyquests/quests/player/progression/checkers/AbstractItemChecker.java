package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.types.BasicQuest;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

import java.util.HashMap;

public abstract class AbstractItemChecker {

    /**
     * Increase player quest progression.
     *
     * @param player    the player to increase progression for.
     * @param itemStack the item to increase progression for.
     * @param amount    the amount to increase progression by.
     * @param questType the quest type to increase progression for.
     */
    public void setPlayerQuestProgression(Player player, ItemStack itemStack, int amount, String questType) {

        Debugger.addDebug("ItemChecker: setPlayerQuestProgression summoned by " + player.getName() + " for " + itemStack.getType() + " with amount " + amount + " and quest type " + questType + ".");

        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            return;
        }

        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {

            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();

            Debugger.addDebug("ItemChecker: player " + player.getName() + "currently have " + playerQuests.size() + " quests in progress");

            for (AbstractQuest abstractQuest : playerQuests.keySet()) {

                final Progression progression = playerQuests.get(abstractQuest);
                if (!progression.isAchieved() && abstractQuest.getQuestType().equals(questType)) {

                    Debugger.addDebug("ItemChecker: player " + player.getName() + " is currently progressing on " + abstractQuest.getQuestType() + " quest " + abstractQuest.getQuestName());

                    boolean isRequiredItem = false;

                    if (abstractQuest instanceof BasicQuest) {
                        isRequiredItem = true;
                    } else if (abstractQuest instanceof ItemQuest quest) {

                        Debugger.addDebug("ItemChecker: quest " + abstractQuest.getQuestName() + " is an ItemQuest");

                        if (quest.getRequiredItems() == null) isRequiredItem = true;
                        else {
                            for (ItemStack item : quest.getRequiredItems()) {

                                if (quest.isIgnoreNbt() && item.getType() == itemStack.getType()) {
                                    isRequiredItem = true;
                                    break;
                                }

                                if (item.hasItemMeta() && item.getItemMeta().hasCustomModelData()) {
                                    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData()) {
                                        if (itemStack.getType() == item.getType() && itemStack.getItemMeta().getCustomModelData() == item.getItemMeta().getCustomModelData()) {
                                            isRequiredItem = true;
                                            break;
                                        }
                                    }
                                } else {
                                    if (item.isSimilar(itemStack)) {
                                        isRequiredItem = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (isRequiredItem) {

                        Debugger.addDebug("ItemChecker: item " + itemStack.getType() + " is a required item for quest " + abstractQuest.getQuestName());

                        PlayerProgressor.actionQuest(player, progression, abstractQuest, amount);
                        if (!Synchronization.isSynchronised()) break;
                    }
                }
            }
        }
    }

    /**
     * @param stack the item to check.
     * @param inv   the inventory to check.
     * @return the amount of items that can be added to the inventory.
     */
    protected int fits(ItemStack stack, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        int result = 0;

        for (ItemStack is : contents)
            if (is == null) result += stack.getMaxStackSize();
            else if (is.isSimilar(stack)) result += Math.max(stack.getMaxStackSize() - is.getAmount(), 0);

        return result;
    }

    /**
     * Returns the maximum amount of items that can be crafted in the given inventory.
     *
     * @param inv the inventory to check.
     * @return the maximum.
     */
    protected int getMaxCraftAmount(CraftingInventory inv) {
        if (inv.getResult() == null) return 0;

        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getMatrix())
            if (is != null && is.getAmount() < materialCount) materialCount = is.getAmount();

        return resultCount * materialCount;
    }

    /**
     * Returns the maximum amount of items that can be smithed in the given inventory.
     *
     * @param inv the inventory to check.
     * @return the maximum.
     */
    protected int getMaxSmithAmount(SmithingInventory inv) {
        if (inv.getResult() == null) return 0;

        int resultCount = inv.getResult().getAmount();
        int materialCount = Integer.MAX_VALUE;

        for (ItemStack is : inv.getContents())
            if (is != null && is.getAmount() < materialCount) materialCount = is.getAmount();

        return resultCount * materialCount;
    }

    /**
     * Checks if the player is moving an item.
     *
     * @param result       the result item.
     * @param recipeAmount the amount of items in the recipe.
     * @param player       the player to check.
     * @param click        the click type.
     * @return true if the player is moving an item.
     */
    protected boolean movingItem(ItemStack result, int recipeAmount, Player player, ClickType click) {
        final ItemStack cursorItem = player.getItemOnCursor();

        if (cursorItem.getType() != Material.AIR) {
            if (cursorItem.getType() == result.getType()) {
                if (cursorItem.getAmount() + recipeAmount > cursorItem.getMaxStackSize()) {
                    return click == ClickType.LEFT || click == ClickType.RIGHT;
                }
            } else return true;
        }
        return false;
    }
}
