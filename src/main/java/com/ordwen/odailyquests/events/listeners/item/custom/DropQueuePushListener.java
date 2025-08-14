package com.ordwen.odailyquests.events.listeners.item.custom;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.types.item.FarmingQuest;
import com.willfp.eco.core.events.DropQueuePushEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class DropQueuePushListener extends PlayerProgressor implements Listener {

    private static BlockState currentState;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropQueuePush(DropQueuePushEvent event) {
        Debugger.write("DropQueuePushListener: onDropQueuePush event summoned.");

        if (event.isCancelled()) {
            Debugger.write("DropQueuePushListener: onDropQueuePush event is cancelled.");
            return;
        }

        final Player player = event.getPlayer();
        final BlockData data = currentState.getBlockData();
        final Material dataMaterial = data.getMaterial();

        Debugger.write("DropQueuePushListener: onDropQueuePush event triggered by player: " + player.getName() + ".");

        final Collection<? extends ItemStack> drops = event.getItems();
        if (drops.isEmpty()) {
            Debugger.write("DropQueuePushListener: onDropQueuePush event no drops.");
            return;
        }

        // check if the dropped item is a crop
        if (isAgeableAndFullyGrown(event, data, dataMaterial, drops)) return;

        // check if the block have been placed by the player
        if (isPlayerPlacedBlock(currentState.getBlock(), dataMaterial)) return;

        // handle remaining drops
        handleDrops(event, drops);

        // check if the dropped item is a block that can be posed
        handleStoreBrokenBlocks(drops, player, dataMaterial);
    }

    /**
     * Check if the block is an ageable crop and if it is fully grown.
     *
     * @param event  the event that triggered the listener
     * @param data   the block data of the broken block
     * @param dataMaterial the material of the block data
     * @param drops  list of dropped items
     * @return true if the block is an ageable crop and fully grown, false otherwise
     */
    private boolean isAgeableAndFullyGrown(DropQueuePushEvent event, BlockData data, Material dataMaterial, Collection<? extends ItemStack> drops) {
        if (data instanceof Ageable ageable) {
            Debugger.write("DropQueuePushListener: onBlockDropItemEvent ageable block: " + dataMaterial + ".");

            if (ageable.getAge() == ageable.getMaximumAge()) {
                Debugger.write("DropQueuePushListener: onBlockDropItemEvent ageable block is mature.");
                handleDrops(event, drops);

                return true;
            } else {
                Debugger.write("DropQueuePushListener: onBlockDropItemEvent ageable block is not mature.");
            }
        }
        return false;
    }

    /**
     * Handle the dropped items and update the player progression.
     *
     * @param event  the event that triggered the listener
     * @param drops  list of dropped items
     */
    private void handleDrops(DropQueuePushEvent event, Collection<? extends ItemStack> drops) {
        Debugger.write("DropQueuePushListener: handleDrops summoned.");
        for (ItemStack item : drops) {
            final Material droppedMaterial = item.getType();
            Debugger.write("DropQueuePushListener: handling drop: " + droppedMaterial + ".");

            FarmingQuest.setCurrent(new ItemStack(droppedMaterial));
            setPlayerQuestProgression(event, event.getPlayer(), item.getAmount(), "FARMING");
        }
    }

    /**
     * Stores metadata on a collection of dropped {@link ItemStack}s to mark them
     * as broken by the specified player.
     * <p>
     * This version is used when the drops are already {@link ItemStack}s, such as in
     * virtual drop systems like {@link DropQueuePushEvent}.
     *
     * @param drops    the collection of dropped {@link ItemStack}s
     * @param player   the player who broke the block
     * @param material the material of the block that was broken
     */
    private void handleStoreBrokenBlocks(Collection<? extends ItemStack> drops, Player player, Material material) {
        if (material.isBlock() && Antiglitch.isStoreBrokenBlocks()) {
            Debugger.write("DropQueuePushListener: onBlockDropItemEvent storing broken block.");
            storeBrokenBlockMetadata(drops, player);
        }
    }

    public static void setCurrentState(BlockState currentState) {
        DropQueuePushListener.currentState = currentState;
    }

    public static BlockState getCurrentState() {
        return currentState;
    }
}
