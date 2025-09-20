package com.ordwen.odailyquests.quests.player.progression;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.api.events.QuestProgressEvent;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressionMessage;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.externs.hooks.Protection;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.item.FarmingQuest;
import com.ordwen.odailyquests.tools.DisplayName;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Manages the progression of a player's quests.
 * <p>
 * This class provides methods to track and update the progression of quests for players, including checking progress,
 * executing actions when a quest is completed, and handling various conditions like world, region, and permissions for progression.
 */
public class PlayerProgressor {

    /**
     * Set the player's progression for a specific quest type
     *
     * @param player    the player to set the progression for
     * @param amount    the amount to set the progression to
     * @param questType the quest type to set the progression for
     */
    public void setPlayerQuestProgression(Event event, Player player, int amount, String questType) {
        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {
            Debugger.write("Active quests contain " + player.getName() + ".");
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
    private void checkForProgress(Event event, Player player, int amount, String questType) {
        final Map<AbstractQuest, Progression> playerQuests = ODailyQuestsAPI.getPlayerQuests(player.getName()).getQuests();
        for (Map.Entry<AbstractQuest, Progression> entry : playerQuests.entrySet()) {
            final AbstractQuest quest = entry.getKey();
            if (quest.getQuestType().equals(questType)) {
                final Progression progression = entry.getValue();
                if (!progression.isAchieved() && quest.canProgress(event, progression)) {
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
    public void actionQuest(Player player, Progression progression, AbstractQuest quest, int amount) {

        Debugger.write("QuestProgressUtils: actionQuest summoned by " + player.getName() + " for " + quest.getQuestName() + " with amount " + amount + ".");

        final QuestProgressEvent event = new QuestProgressEvent(player, progression, quest, amount);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            Debugger.write("QuestProgressUtils: QuestProgressEvent is not cancelled.");
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
    private void runProgress(Player player, Progression progression, AbstractQuest quest, int amount) {
        if (QuestLoaderUtils.isTimeToRenew(player, QuestsManager.getActiveQuests())) return;
        if (!isAllowedToProgress(player, quest)) return;

        final String questName = quest.getQuestName().replace("%displayName%", DisplayName.getDisplayName(quest, progression.getSelectedRequiredIndex()));

        final int current = progression.getAdvancement();
        final int required = progression.getRequiredAmount();

        // never exceed the required amount
        final int remaining = required - current;
        final int toAdd = Math.min(amount, remaining);

        for (int i = 0; i < toAdd; i++) {
            Debugger.write("QuestProgressUtils: increasing progression for " + questName + " by " + amount + ".");
            progression.increaseAdvancement();
        }

        if (progression.getAdvancement() >= required) {
            Debugger.write("QuestProgressUtils: progression " + progression.getAdvancement() + " is greater than or equal to amount required " + progression.getRequiredAmount() + ".");
            ODailyQuests.morePaperLib.scheduling().globalRegionalScheduler().runDelayed(() -> {
                Debugger.write("QuestProgressUtils: QuestCompletedEvent is called.");
                final QuestCompletedEvent completedEvent = new QuestCompletedEvent(player, progression, quest);
                Bukkit.getPluginManager().callEvent(completedEvent);
            }, 1L);

            return;
        }

        ProgressionMessage.sendProgressionMessage(player, questName, progression.getAdvancement(), progression.getRequiredAmount());
    }

    /**
     * Execute all the checks to determine if the player is allowed to progress in the quest. This includes checking if the player is in the required world and region.
     *
     * @param player the player to check.
     * @param quest  the quest to check.
     * @return true if the player is allowed to progress.
     */
    public boolean isAllowedToProgress(Player player, AbstractQuest quest) {
        if (!player.hasPermission("odailyquests.progress")) {
            Debugger.write("PlayerProgressor: isAllowedToProgress cancelled due to missing permission.");
            return false;
        }

        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            Debugger.write("PlayerProgressor: isAllowedToProgress cancelled due to disabled world.");
            return false;
        }

        /* check if player is in the required world */
        if (!quest.getRequiredWorlds().isEmpty() && !quest.getRequiredWorlds().contains(player.getWorld().getName())) {
            final String msg = QuestsMessages.NOT_REQUIRED_WORLD.getMessage(player);
            if (msg != null) player.sendMessage(msg);
            return false;
        }

        /* check if player is in the required region */
        if (!quest.getRequiredRegions().isEmpty() && !Protection.checkRegion(player, quest.getRequiredRegions())) {
            final String msg = QuestsMessages.NOT_REQUIRED_REGION.getMessage(player);
            if (msg != null) player.sendMessage(msg);
            return false;
        }

        return true;
    }

    /**
     * @param stack    the item to check.
     * @param contents the inventory contents to compare.
     * @return the amount of items that can be added to the inventory.
     */
    public int fits(ItemStack stack, ItemStack[] contents) {
        int result = 0;

        for (ItemStack is : contents) {
            if (is == null) {
                result += stack.getMaxStackSize();
            } else if (is.isSimilar(stack)) {
                result += Math.max(stack.getMaxStackSize() - is.getAmount(), 0);
            }
        }

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

    /**
     * Check if the block has been placed by the player.
     *
     * @param block    the block to check
     * @param material the material of the block
     * @return true if the block has been placed by the player
     */
    protected boolean isPlayerPlacedBlock(Block block, Material material) {
        if (material.isBlock() && Antiglitch.isStorePlacedBlocks()) {
            final PersistentDataContainer pdc = new CustomBlockData(block, ODailyQuests.INSTANCE);
            // check if type has changed
            final String previousType = pdc.getOrDefault(Antiglitch.PLACED_KEY, PersistentDataType.STRING, material.name());
            if (previousType.equals(material.name())) {
                Debugger.write("PlayerProgressor: onBlockDropItemEvent cancelled, material equal to previous type. Maybe BREAK type should be used instead?");
                return true;
            } else {
                Debugger.write("PlayerProgressor: onBlockDropItemEvent block type has changed (" + previousType + " -> " + material.name() + ").");
            }
        }  else {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent not storing placed blocks, or material is not a block.");
        }
        return false;
    }

    /**
     * Handle the dropped items and update the player progression.
     *
     * @param event  the event that triggered the listener
     * @param player involved player in the event
     * @param drops  list of dropped items
     */
    protected void handleDrops(Event event, Player player, List<Item> drops) {
        Debugger.write("PlayerProgressor: handleDrops summoned.");
        for (Item item : drops) {
            final ItemStack droppedItem = item.getItemStack();
            final Material droppedMaterial = droppedItem.getType();
            Debugger.write("PlayerProgressor: handling drop: " + droppedMaterial + ".");

            FarmingQuest.setCurrent(new ItemStack(droppedMaterial));
            setPlayerQuestProgression(event, player, droppedItem.getAmount(), "FARMING");
        }
    }

    /**
     * Adds persistent metadata to each {@link ItemStack} in the given collection to
     * indicate that the block was broken by the specified player.
     * <p>
     * This metadata is used by anti-glitch mechanisms to track legitimate block drops
     * and prevent duplication or exploitation.
     *
     * @param drops  the collection of dropped {@link ItemStack}s
     * @param player the player who broke the original block
     */
    protected void storeBrokenBlockMetadata(Collection<? extends ItemStack> drops, Player player) {
        for (ItemStack drop : drops) {
            Debugger.write("PlayerProgressor: onBlockDropItemEvent storing broken block: " + drop.getType());
            final ItemMeta dropMeta = drop.getItemMeta();
            if (dropMeta == null) continue;

            final PersistentDataContainer pdc = dropMeta.getPersistentDataContainer();
            pdc.set(Antiglitch.BROKEN_KEY, PersistentDataType.STRING, player.getUniqueId().toString());
            drop.setItemMeta(dropMeta);
        }
    }
}
