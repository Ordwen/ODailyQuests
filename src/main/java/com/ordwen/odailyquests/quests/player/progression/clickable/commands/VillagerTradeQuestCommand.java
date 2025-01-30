package com.ordwen.odailyquests.quests.player.progression.clickable.commands;

import com.ordwen.odailyquests.events.antiglitch.OpenedRecipes;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestCommand;
import com.ordwen.odailyquests.quests.player.progression.clickable.QuestContext;
import com.ordwen.odailyquests.quests.types.item.VillagerQuest;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

public class VillagerTradeQuestCommand extends QuestCommand<VillagerQuest> {

    public VillagerTradeQuestCommand(QuestContext context, Progression progression, VillagerQuest quest) {
        super(context, progression, quest);
    }

    /**
     * Validate VILLAGER_TRADE quest type.
     */
    @Override
    public void execute() {
        if (!quest.isAllowedToProgress(context.getPlayer(), quest)) return;

        final MerchantRecipe selectedRecipe = context.getSelectedRecipe();
        if (selectedRecipe == null) return;

        if (!isValidTrade(selectedRecipe)) return;

        final Villager villager = context.getVillager();
        if (villager != null && !isValidVillager(villager)) return;

        quest.actionQuest(context.getPlayer(), progression, quest, context.getQuantity());
    }

    /**
     * Check if the trade is valid based on required items and usage limit.
     */
    private boolean isValidTrade(MerchantRecipe selectedRecipe) {
        if (quest.getRequiredItems() != null && !isValidRequiredItem(selectedRecipe)) {
            return false;
        }

        return selectedRecipe.getUses() > OpenedRecipes.get(selectedRecipe);
    }

    /**
     * Check if the required items match the trade result.
     */
    private boolean isValidRequiredItem(MerchantRecipe selectedRecipe) {
        for (ItemStack item : quest.getRequiredItems()) {
            if (item.getType() == selectedRecipe.getResult().getType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the villager is valid based on the quest's profession and level.
     */
    private boolean isValidVillager(Villager villager) {
        return isValidProfession(villager) && isValidLevel(villager);
    }

    /**
     * Check if the villager's profession matches the quest's profession.
     */
    private boolean isValidProfession(Villager villager) {
        return quest.getVillagerProfession() == null || quest.getVillagerProfession().equals(villager.getProfession());
    }

    /**
     * Check if the villager's level matches the quest's level.
     */
    private boolean isValidLevel(Villager villager) {
        return quest.getVillagerLevel() == 0 || quest.getVillagerLevel() == villager.getVillagerLevel();
    }
}
