package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.api.events.QuestProgressEvent;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressionMessage;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PlayerProgressor {

    /**
     * Set the player's progression for a specific quest type
     *
     * @param player    the player to set the progression for
     * @param amount    the amount to set the progression to
     * @param questType the quest type to set the progression for
     */
    public void setPlayerQuestProgression(Event event, Player player, int amount, String questType) {
        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            Debugger.addDebug("PlayerProgressor: setPlayerQuestProgression cancelled due to disabled world.");
            return;
        }

        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {
            Debugger.addDebug("Active quests contain " + player.getName() + ".");
            checkForProgress(event, player, amount, questType);
        }
    }

    /**
     * Check for progress for a specific quest type
     *
     * @param event     the event that triggered the progression
     * @param player    the player to check for progress
     * @param amount    the amount of progression
     * @param questType the quest type to check for
     */
    private static void checkForProgress(Event event, Player player, int amount, String questType) {
        final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();
        for (Map.Entry<AbstractQuest, Progression> entry : playerQuests.entrySet()) {
            final AbstractQuest quest = entry.getKey();
            if (quest.getQuestType().equals(questType)) {
                final Progression progression = entry.getValue();
                if (!progression.isAchieved() && quest.canProgress(event)) {
                    actionQuest(player, progression, quest, amount);
                    if (!Synchronization.isSynchronised()) break;
                }
            }
        }
    }

    /**
     * Raises the QuestProgressEvent event and determines whether to perform progress based on the event result.
     *
     * @param player      involved player
     * @param progression player's progression
     * @param quest       quest to be progressed
     * @param amount      amount of progression
     */
    public static void actionQuest(Player player, Progression progression, AbstractQuest quest, int amount) {

        Debugger.addDebug("QuestProgressUtils: actionQuest summoned by " + player.getName() + " for " + quest.getQuestName() + " with amount " + amount + ".");

        final QuestProgressEvent event = new QuestProgressEvent(player, progression, quest, amount);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            Debugger.addDebug("QuestProgressUtils: QuestProgressEvent is not cancelled.");
            runProgress(player, progression, quest, amount);
        }
    }

    /**
     * Increases quest progress.
     *
     * @param player      involved player
     * @param progression player's progression
     * @param quest       quest to be progressed
     * @param amount      amount of progression
     */
    private static void runProgress(Player player, Progression progression, AbstractQuest quest, int amount) {
        if (QuestLoaderUtils.isTimeToRenew(player, QuestsManager.getActiveQuests())) return;

        if (!quest.getRequiredWorlds().isEmpty() && !quest.getRequiredWorlds().contains(player.getWorld().getName())) {
            final String msg = QuestsMessages.NOT_REQUIRED_WORLD.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            return;
        }

        for (int i = 0; i < amount; i++) {
            Debugger.addDebug("QuestProgressUtils: increasing progression for " + quest.getQuestName() + " by " + amount + ".");
            progression.increaseProgression();
        }

        if (progression.getProgression() >= quest.getAmountRequired()) {
            Debugger.addDebug("QuestProgressUtils: progression " + progression.getProgression() + " is greater than or equal to amount required " + quest.getAmountRequired() + ".");
            Bukkit.getScheduler().runTaskLater(ODailyQuests.INSTANCE, () -> {
                Debugger.addDebug("QuestProgressUtils: QuestCompletedEvent is called.");
                final QuestCompletedEvent completedEvent = new QuestCompletedEvent(player, progression, quest);
                Bukkit.getPluginManager().callEvent(completedEvent);
            }, 1L);

            return;
        }

        ProgressionMessage.sendProgressionMessage(player, quest.getQuestName(), progression.getProgression(), quest.getAmountRequired());
    }

    /**
     * @param stack the item to check.
     * @param inv   the inventory to check.
     * @return the amount of items that can be added to the inventory.
     */
    public int fits(ItemStack stack, Inventory inv) {
        ItemStack[] contents = inv.getContents();
        int result = 0;

        for (ItemStack is : contents)
            if (is == null) result += stack.getMaxStackSize();
            else if (is.isSimilar(stack)) result += Math.max(stack.getMaxStackSize() - is.getAmount(), 0);

        return result;
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
    public boolean movingItem(ItemStack result, int recipeAmount, Player player, ClickType click) {
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
