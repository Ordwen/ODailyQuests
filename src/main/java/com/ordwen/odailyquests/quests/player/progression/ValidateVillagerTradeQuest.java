package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.quests.Quest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.MerchantRecipe;

import java.util.HashMap;

public class ValidateVillagerTradeQuest implements Listener {

    /* Stock all recipes and their current uses from open villagers menus */
    private static final HashMap<MerchantRecipe, Integer> openRecipes = new HashMap<>();

    /* Add recipes to the map when a villager is clicked */
    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager villager) {
            for (MerchantRecipe recipe : villager.getRecipes()) {
                openRecipes.put(recipe, recipe.getUses() - 1);
            }
        }
    }

    /* Remove recipes from the map when villager menu is closed */
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT
                && event.getInventory().getHolder() instanceof Villager villager) {
            for (MerchantRecipe recipe : villager.getRecipes()) {
                openRecipes.remove(recipe);
            }
        }
    }

    /**
     * Validate VILLAGER_TRADE quest type.
     *
     * @param playerName     player who is trading.
     * @param villager       villager wich one the player is trading.
     * @param selectedRecipe item trade.
     * @param quantity       quantity trade.
     */
    public static void validateTradeQuestType(String playerName, Villager villager, MerchantRecipe selectedRecipe, int quantity) {
        if (QuestsManager.getActiveQuests().containsKey(playerName)) {

            HashMap<Quest, Progression> playerQuests = QuestsManager.getActiveQuests().get(playerName).getPlayerQuests();

            for (Quest quest : playerQuests.keySet()) {
                boolean valid = false;
                Progression questProgression = playerQuests.get(quest);
                if (!questProgression.isAchieved() && quest.getType() == QuestType.VILLAGER_TRADE) {

                    if (quest.getItemRequired() == null) valid = true;
                    else if (quest.getItemRequired().getType() == selectedRecipe.getResult().getType()) valid = true;

                    if (selectedRecipe.getUses() <= openRecipes.get(selectedRecipe)) {
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
                            questProgression.progression++;
                        }
                        if (questProgression.getProgression() >= quest.getAmountRequired()) {
                            questProgression.isAchieved = true;
                            QuestsManager.getActiveQuests().get(playerName).increaseAchievedQuests(playerName);
                            RewardManager.sendAllRewardItems(quest.getQuestName(), playerName, quest.getReward());
                        }

                        if (!Synchronization.isSynchronised()) break;
                    }
                }
            }
        }
    }
}
