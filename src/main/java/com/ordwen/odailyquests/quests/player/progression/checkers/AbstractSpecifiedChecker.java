package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.TakeItems;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.events.antiglitch.OpenedRecipes;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.AbstractProgressionIncreaser;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.*;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;

public abstract class AbstractSpecifiedChecker extends AbstractProgressionIncreaser {

    /**
     * Increase player quest progression.
     *
     * @param player the player to increase progression for.
     */
    public void setPlayerQuestProgression(Player player, ItemStack clickedItem) {
        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            return;
        }

        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {

            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();
            for (AbstractQuest abstractQuest : playerQuests.keySet()) {

                if (abstractQuest instanceof ItemQuest quest) {
                    if (clickedItem.equals(quest.getMenuItem()) && quest.getType() == QuestType.GET) {

                        final Progression progression = playerQuests.get(abstractQuest);
                        if (!progression.isAchieved()) {
                            validateGetQuestType(player, progression, quest);
                        }
                        break;
                    }
                }
                // TEMP TO DELETE =========================================================
                else if (abstractQuest instanceof PotionQuest quest) {
                    if (clickedItem.equals(quest.getMenuItem()) && quest.getType() == QuestType.GET) {

                        final Progression progression = playerQuests.get(abstractQuest);
                        if (!progression.isAchieved()) {
                            validateGetQuestTypePotion(player, progression, quest);
                        }
                        break;
                    }
                }
                // TEMP TO DELETE =========================================================
                else if (abstractQuest instanceof LocationQuest quest) {

                    if (clickedItem.equals(quest.getMenuItem()) && quest.getType() == QuestType.LOCATION) {

                        final Progression progression = playerQuests.get(quest);
                        if (!progression.isAchieved()) {
                            validateLocationQuestType(player, progression, quest);
                        }
                    }
                }
            }
        }
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
        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {

            HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();

            for (AbstractQuest abstractQuest : playerQuests.keySet()) {
                if (abstractQuest instanceof VillagerQuest quest) {
                    boolean valid = false;
                    Progression questProgression = playerQuests.get(quest);
                    if (!questProgression.isAchieved() && quest.getType() == QuestType.VILLAGER_TRADE) {

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
                                    if (!(quest.getVillagerLevel() == villager.getVillagerLevel())) {
                                        valid = false;
                                    }
                                }
                            }
                        }

                        if (valid) {
                            increaseProgression(player, questProgression, quest, quantity);
                            if (!Synchronization.isSynchronised()) break;
                        }
                    }
                }
            }
        }
    }

    // TEMP TO DELETE =========================================================
    /**
     * Validate GET quest type.
     *
     * @param player      player who is getting the item.
     * @param progression progression of the quest.
     * @param quest       quest to validate.
     */
    private void validateGetQuestTypePotion(Player player, Progression progression, PotionQuest quest) {
        boolean hasRequiredAmount = true;
        for (ItemStack item : quest.getRequiredItems()) {
            if (getAmount(player.getInventory(), item) < quest.getAmountRequired()) {
                hasRequiredAmount = false;
            }
        }

        if (hasRequiredAmount) {
            progression.setAchieved();
            QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());

            if (TakeItems.isTakeItemsEnabled()) {
                for (ItemStack item : quest.getRequiredItems()) {
                    final ItemStack toRemove = item.clone();
                    toRemove.setAmount(quest.getAmountRequired());
                    player.getInventory().removeItem(toRemove);
                }
            }

            player.closeInventory();
            RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());
        } else {
            final String msg = QuestsMessages.NOT_ENOUGH_ITEM.toString();
            if (msg != null) player.sendMessage(msg);
        }
    }
    // TEMP TO DELETE =========================================================


    /**
     * Validate GET quest type.
     *
     * @param player      player who is getting the item.
     * @param progression progression of the quest.
     * @param quest       quest to validate.
     */
    private void validateGetQuestType(Player player, Progression progression, ItemQuest quest) {
        boolean hasRequiredAmount = true;
        for (ItemStack item : quest.getRequiredItems()) {
            if (getAmount(player.getInventory(), item) < quest.getAmountRequired()) {
                hasRequiredAmount = false;
            }
        }

        if (hasRequiredAmount) {
            progression.setAchieved();
            QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());

            if (TakeItems.isTakeItemsEnabled()) {
                for (ItemStack item : quest.getRequiredItems()) {
                    final ItemStack toRemove = item.clone();
                    toRemove.setAmount(quest.getAmountRequired());
                    player.getInventory().removeItem(toRemove);
                }
            }

            player.closeInventory();
            RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());
        } else {
            final String msg = QuestsMessages.NOT_ENOUGH_ITEM.toString();
            if (msg != null) player.sendMessage(msg);
        }
    }

    /**
     * Validate LOCATION quest type.
     *
     * @param player      player who is on the location.
     * @param progression progression of the quest.
     * @param quest       quest to validate.
     */
    private void validateLocationQuestType(Player player, Progression progression, LocationQuest quest) {
        final Location requiredLocation = quest.getRequiredLocation();
        double distance = player.getLocation().distance(requiredLocation);
        if (distance <= quest.getRadius()) {
            if (!requiredLocation.getWorld().equals(player.getLocation().getWorld())) {
                final String msg = QuestsMessages.BAD_WORLD_LOCATION.toString();
                if (msg != null) player.sendMessage(msg);
                return;
            }
            progression.setAchieved();
            QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());

            player.closeInventory();
            RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());
        } else {
            final String msg = QuestsMessages.TOO_FAR_FROM_LOCATION.toString();
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
    private int getAmount(PlayerInventory playerInventory, ItemStack item) {
        int amount = 0;
        for (ItemStack itemStack : playerInventory.getContents()) {
            if (itemStack != null && itemStack.isSimilar(item)) amount += itemStack.getAmount();
        }
        return amount;
    }
}
