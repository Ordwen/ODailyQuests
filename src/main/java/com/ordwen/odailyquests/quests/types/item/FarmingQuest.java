package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.events.listeners.item.custom.DropQueuePushListener;
import com.ordwen.odailyquests.externs.hooks.Protection;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import com.ordwen.odailyquests.tools.PluginUtils;
import com.willfp.eco.core.events.DropQueuePushEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a farming-related quest that requires the player to interact with crops or blocks.
 * <p>
 * This class handles different block-related events (harvest, break, drop, etc.) to determine
 * whether a player has made progress on a farming quest.
 * <p>
 * The quest is defined by a static {@link ItemStack} reference called {@code current}, used to compare
 * dropped or harvested items against the quest objective.
 */
public class FarmingQuest extends ItemQuest {

    private static ItemStack current;

    /**
     * Constructs a new {@code FarmingQuest} from a base quest configuration.
     *
     * @param base the base quest data containing metadata and objectives
     */
    public FarmingQuest(BasicQuest base) {
        super(base);
    }

    /**
     * Returns the type identifier of this quest.
     *
     * @return the string {@code "FARMING"}
     */
    @Override
    public String getType() {
        return "FARMING";
    }

    /**
     * Determines whether the player has made progress in the farming quest based on the provided event.
     * <p>
     * This method supports several types of block-related events:
     * <ul>
     *     <li>{@link PlayerHarvestBlockEvent} — triggered when the player harvests an ageable block (e.g., wheat).</li>
     *     <li>{@link BlockDropItemEvent} — triggered when breaking a block that drops items (ignores containers).</li>
     *     <li>{@link BlockBreakEvent} — triggered for general block breaks (used for vertical crops like sugar cane).</li>
     *     <li>{@link DropQueuePushEvent} — triggered by external plugins like Eco to simulate block drops.</li>
     * </ul>
     * <p>
     * Depending on the event, the quest compares either the static target {@link #current} or a normalized version
     * of the block type to determine if it matches the required quest item.
     *
     * @param provided    the event that may indicate player progression
     * @param progression the current progression state of the player
     * @return {@code true} if the event corresponds to the expected item and the player is allowed to build; {@code false} otherwise
     */
    @Override
    public boolean canProgress(Event provided, Progression progression) {
        if (provided instanceof PlayerHarvestBlockEvent event) {
            return checkBlockAndProgress(event.getPlayer(), event.getHarvestedBlock(), progression, false, current);
        }

        if (provided instanceof BlockDropItemEvent event) {
            return checkBlockAndProgress(event.getPlayer(), event.getBlock(), progression, true, current);
        }

        if (provided instanceof BlockBreakEvent event) {
            return checkBlockAndProgress(event.getPlayer(), event.getBlock(), progression, false, null);
        }

        if (PluginUtils.isPluginEnabled("eco") && provided instanceof DropQueuePushEvent event) {
            return checkBlockAndProgress(event.getPlayer(), DropQueuePushListener.getCurrentState().getBlock(), progression, false, current);
        }

        return false;
    }

    /**
     * Validates if a player can progress in the farming quest based on a given block and progression state.
     * <p>
     * If {@code referenceItem} is provided, it is used directly to compare against the quest requirement.
     * Otherwise, the block's material is normalized (e.g., removing "_PLANT") to create an {@link ItemStack}.
     * <p>
     * Optionally checks if the block is a container and should be rejected.
     *
     * @param player           the player who triggered the event
     * @param block            the block involved in the event
     * @param progression      the player's progression state
     * @param rejectContainers true to reject container blocks (e.g., chests)
     * @param referenceItem    optional predefined {@link ItemStack} to use for comparison; if {@code null}, the block's material will be used
     * @return true if the block passes all checks and matches the quest item; false otherwise
     */
    private boolean checkBlockAndProgress(Player player, Block block, Progression progression, boolean rejectContainers, @Nullable ItemStack referenceItem) {
        if (rejectContainers && block.getState() instanceof InventoryHolder) {
            Debugger.write("FarmingQuest:canProgress: Block is a container.");
            return false;
        }

        if (!this.isProtectionBypass() && !Protection.canBuild(player, block, "BLOCK_BREAK")) {
            return false;
        }

        final ItemStack itemToCompare;

        if (referenceItem != null) {
            itemToCompare = referenceItem;
        } else {
            itemToCompare = getNormalizedBlockItem(block.getType());
            if (itemToCompare == null) return false;
        }

        return super.isRequiredItem(itemToCompare, progression);
    }

    /**
     * Normalizes a block {@link Material} to its corresponding item form for farming progression.
     * <p>
     * If the block type ends with {@code "_PLANT"}, the suffix is removed to derive the base item type.
     * This is useful for handling vertical plants like {@code WHEAT_PLANT} or {@code BEETROOTS_PLANT}.
     * <p>
     * If the derived material is not a valid item (e.g., not present in {@link Material#getMaterial(String)}
     * or {@link Material#isItem()} returns false), {@code null} is returned.
     *
     * @param material the original {@link Material} of the broken block
     * @return a corresponding {@link ItemStack} if the material is valid and represents an item; otherwise {@code null}
     */
    private @Nullable ItemStack getNormalizedBlockItem(Material material) {
        String blockType = material.name();

        if (blockType.endsWith("_PLANT")) {
            Debugger.write("FarmingQuest:canProgress: Block is a plant: " + blockType + ".");
            blockType = blockType.substring(0, blockType.length() - 6);
        }

        Debugger.write("FarmingQuest:canProgress: Potential vertical plant. Checking for type " + blockType + ".");

        Material itemMaterial = Material.getMaterial(blockType);
        if (itemMaterial == null || !itemMaterial.isItem()) {
            Debugger.write("FarmingQuest:canProgress: Material " + blockType + " is not valid.");
            return null;
        }

        return new ItemStack(itemMaterial);
    }

    /**
     * Sets the static reference {@code current} used to compare against block drops for progression.
     *
     * @param current the {@link ItemStack} used as the farming quest target
     */
    public static void setCurrent(ItemStack current) {
        FarmingQuest.current = current;
    }
}
