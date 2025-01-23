package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.events.antiglitch.OpenedRecipes;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.*;
import com.ordwen.odailyquests.quests.types.inventory.GetQuest;
import com.ordwen.odailyquests.quests.types.inventory.LocationQuest;
import com.ordwen.odailyquests.quests.types.inventory.PlaceholderQuest;
import com.ordwen.odailyquests.quests.types.item.VillagerQuest;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractClickableChecker extends PlayerProgressor {

    /**
     * Increase player quest progression.
     *
     * @param player the player to increase progression for.
     */
    public void setPlayerQuestProgression(Player player, ItemStack clickedItem) {
        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            final String msg = QuestsMessages.WORLD_DISABLED.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            return;
        }

        final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();
        for (Map.Entry<AbstractQuest, Progression> entry : playerQuests.entrySet()) {
            final AbstractQuest abstractQuest = entry.getKey();
            final Progression progression = entry.getValue();

            if (progression.isAchieved()) continue;

            if (abstractQuest instanceof GetQuest quest) {
                if (isAppropriateQuestMenuItem(clickedItem, quest.getMenuItem()) && quest.getQuestType().equals("GET")) {
                    new GetQuestChecker(player, progression, quest).validateAndComplete();
                }
            } else if (abstractQuest instanceof LocationQuest quest) {
                if (isAppropriateQuestMenuItem(clickedItem, quest.getMenuItem()) && quest.getQuestType().equals("LOCATION")) {
                    new LocationQuestChecker(player, progression, quest).validateAndComplete();
                }
            } else if (abstractQuest instanceof PlaceholderQuest quest) {
                if (isAppropriateQuestMenuItem(clickedItem, quest.getMenuItem()) && quest.getQuestType().equals("PLACEHOLDER")) {
                    new PlaceholderQuestChecker(player, progression, quest).validateAndComplete();
                }
            }
        }
    }

    /**
     * Check if the clicked item is corresponding to a quest menu item, by checking the persistent data container.
     *
     * @param clickedItem clicked item to check.
     * @param menuItem    quest menu item to compare.
     * @return true if the clicked item is a GET quest menu item.
     */
    private boolean isAppropriateQuestMenuItem(ItemStack clickedItem, ItemStack menuItem) {
        final ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        final ItemMeta menuItemMeta = menuItem.getItemMeta();

        if (clickedItem.getType() != menuItem.getType()) return false;
        if (clickedItemMeta == null || menuItemMeta == null) return false;

        return clickedItemMeta.getPersistentDataContainer().equals(menuItemMeta.getPersistentDataContainer());
    }

    /**
     * Validate VILLAGER_TRADE quest type.
     *
     * @param player         player who is trading.
     * @param villager       villager which one the player is trading.
     * @param selectedRecipe item trade.
     * @param quantity       quantity trade.
     */
    public void validateTradeQuestType(Player player, Villager villager, MerchantRecipe selectedRecipe, int quantity) {

        final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();
        for (AbstractQuest abstractQuest : playerQuests.keySet()) {
            if (abstractQuest instanceof VillagerQuest quest) {
                boolean valid = false;
                final Progression questProgression = playerQuests.get(quest);
                if (!questProgression.isAchieved() && quest.getQuestType().equals("VILLAGER_TRADE")) {
                    if (!isAllowedToProgress(player, quest)) return;

                    if (quest.getRequiredItems() == null) valid = true;
                    else {
                        for (ItemStack item : quest.getRequiredItems()) {
                            if (item.getType() == selectedRecipe.getResult().getType()) valid = true;
                        }
                    }

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
                                if (quest.getVillagerLevel() != villager.getVillagerLevel()) {
                                    valid = false;
                                }
                            }
                        }
                    }

                    if (valid) {
                        actionQuest(player, questProgression, quest, quantity);
                        if (!Synchronization.isSynchronised()) break;
                    }
                }
            }
        }
    }
}
