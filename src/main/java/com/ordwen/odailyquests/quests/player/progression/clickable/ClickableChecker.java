package com.ordwen.odailyquests.quests.player.progression.clickable;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.clickable.commands.*;
import com.ordwen.odailyquests.quests.types.*;
import com.ordwen.odailyquests.quests.types.inventory.GetQuest;
import com.ordwen.odailyquests.quests.types.inventory.LocationQuest;
import com.ordwen.odailyquests.quests.types.inventory.PlaceholderQuest;
import com.ordwen.odailyquests.quests.types.item.VillagerQuest;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public abstract class ClickableChecker extends PlayerProgressor {

    public void processQuestCompletion(QuestContext context) {
        final Player player = context.getPlayer();
        final ItemStack clickedItem = context.getClickedItem();
        final Villager villager = context.getVillager();

        if (isWorldDisabled(player)) return;

        final Map<AbstractQuest, Progression> playerQuests = ODailyQuestsAPI.getPlayerQuests(player.getName()).getQuests();

        for (Map.Entry<AbstractQuest, Progression> entry : playerQuests.entrySet()) {
            final AbstractQuest abstractQuest = entry.getKey();
            final Progression progression = entry.getValue();

            if (progression.isAchieved()) continue;

            QuestCommand<? extends AbstractQuest> command = createCommandForQuest(abstractQuest, progression, context, clickedItem, villager);
            if (command != null) {
                command.execute();
            }
        }
    }

    /**
     * Checks if the world is disabled for quest processing.
     */
    private boolean isWorldDisabled(Player player) {
        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            String msg = QuestsMessages.WORLD_DISABLED.getMessage(player);
            if (msg != null) player.sendMessage(msg);
            return true;
        }
        return false;
    }

    /**
     * Creates a command for the given quest type.
     */
    private QuestCommand<? extends AbstractQuest> createCommandForQuest(AbstractQuest abstractQuest, Progression progression, QuestContext context, ItemStack clickedItem, Villager villager) {
        if (abstractQuest instanceof GetQuest getQuest) {
            return createGetQuestCommand(getQuest, clickedItem, context, progression);
        } else if (abstractQuest instanceof LocationQuest locationQuest) {
            return createLocationQuestCommand(locationQuest, clickedItem, context, progression);
        } else if (abstractQuest instanceof PlaceholderQuest placeholderQuest) {
            return createPlaceholderQuestCommand(placeholderQuest, clickedItem, context, progression);
        } else if (abstractQuest instanceof VillagerQuest villagerQuest && villager != null && villagerQuest.getQuestType().equals("VILLAGER_TRADE")) {
            return new VillagerTradeQuestCommand(context, progression, villagerQuest);
        }
        return null;
    }

    /**
     * Creates a GetQuest command if the item matches.
     */
    private QuestCommand<GetQuest> createGetQuestCommand(GetQuest quest, ItemStack clickedItem, QuestContext context, Progression progression) {
        if (isAppropriateQuestMenuItem(clickedItem, quest.getMenuItem()) && quest.getQuestType().equals("GET")) {
            return new GetQuestCommand(context, progression, quest);
        }
        return null;
    }

    /**
     * Creates a LocationQuest command if the item matches.
     */
    private QuestCommand<LocationQuest> createLocationQuestCommand(LocationQuest quest, ItemStack clickedItem, QuestContext context, Progression progression) {
        if (isAppropriateQuestMenuItem(clickedItem, quest.getMenuItem()) && quest.getQuestType().equals("LOCATION")) {
            return new LocationQuestCommand(context, progression, quest);
        }
        return null;
    }

    /**
     * Creates a PlaceholderQuest command if the item matches.
     */
    private QuestCommand<PlaceholderQuest> createPlaceholderQuestCommand(PlaceholderQuest quest, ItemStack clickedItem, QuestContext context, Progression progression) {
        if (isAppropriateQuestMenuItem(clickedItem, quest.getMenuItem()) && quest.getQuestType().equals("PLACEHOLDER")) {
            return new PlaceholderQuestCommand(context, progression, quest);
        }
        return null;
    }

    /**
     * Check if the clicked item is corresponding to a quest menu item, by checking the persistent data container.
     *
     * @param clickedItem clicked item to check.
     * @param menuItem    quest menu item to compare.
     * @return true if the clicked item is a GET quest menu item.
     */
    private boolean isAppropriateQuestMenuItem(ItemStack clickedItem, ItemStack menuItem) {
        if (clickedItem == null || menuItem == null) return false;

        final ItemMeta clickedItemMeta = clickedItem.getItemMeta();
        final ItemMeta menuItemMeta = menuItem.getItemMeta();

        if (clickedItem.getType() != menuItem.getType()) return false;
        if (clickedItemMeta == null || menuItemMeta == null) return false;

        return clickedItemMeta.getPersistentDataContainer().equals(menuItemMeta.getPersistentDataContainer());
    }
}
