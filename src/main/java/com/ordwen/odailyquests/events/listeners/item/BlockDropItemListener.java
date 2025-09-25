package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.events.listeners.item.custom.DropQueuePushListener;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.tools.PluginUtils;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockDropItemListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDropItem(BlockDropItemEvent event) {
        Debugger.write("BlockDropItemListener: onBlockDropItemEvent summoned.");

        if (event.isCancelled()) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent is cancelled.");
            return;
        }

        final Player player = event.getPlayer();
        final BlockData data = event.getBlockState().getBlockData();
        final Material dataMaterial = data.getMaterial();

        if (isVerticalPlant(dataMaterial)) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent vertical plant detected, skipping drop handling.");
            return;
        }

        // fix attempt for eco plugins compatibility issue
        if (PluginUtils.isPluginEnabled("eco")) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent eco is enabled. Setting current state for DropQueuePushListener.");
            DropQueuePushListener.setCurrentState(event.getBlockState());
        }

        Debugger.write("BlockDropItemListener: onBlockDropItemEvent block data: " + dataMaterial.name() + ".");

        final List<Item> drops = event.getItems();
        if (drops.isEmpty()) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent no drops.");
            return;
        }

        // check if the dropped item is a crop
        if (isAgeableAndFullyGrown(event, data, dataMaterial, player, drops)) return;

        // check if the block have been placed by the player
        if (isPlayerPlacedBlock(event.getBlock(), dataMaterial)) return;

        // handle remaining drops
        handleDrops(event, player, drops);

        // check if the dropped item is a block that can be posed
        handleStoreBrokenBlocks(drops, player, dataMaterial);
    }

    /**
     * Check if the ageable block is fully grown and handle the drops accordingly.
     *
     * @param event        the event that triggered the listener
     * @param data         the block data of the broken block
     * @param dataMaterial the material of the block data
     * @param player       involved player in the event
     * @param drops        list of dropped items
     * @return true if the block is ageable and fully grown, false otherwise
     */
    private boolean isAgeableAndFullyGrown(Event event, BlockData data, Material dataMaterial, Player player, List<Item> drops) {
        if (isVerticalPlant(dataMaterial)) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent vertical plant detected, skipping ageable check.");
            return false;
        }

        if (data instanceof Ageable ageable) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent ageable block: " + dataMaterial + ".");

            if (ageable.getAge() == ageable.getMaximumAge()) {
                Debugger.write("BlockDropItemListener: onBlockDropItemEvent ageable block is mature.");
                handleDrops(event, player, drops);

                return true;
            }
        }
        return false;
    }

    /**
     * Converts dropped in-world items to {@link ItemStack}s and stores metadata
     * indicating the block was broken by the specified player.
     * <p>
     * This method is used in event contexts where drops are {@link Item} entities,
     * such as {@link org.bukkit.event.block.BlockDropItemEvent}.
     *
     * @param drops    the list of dropped {@link Item} entities from the event
     * @param player   the player who broke the block
     * @param material the material of the block that was broken
     */
    private void handleStoreBrokenBlocks(List<Item> drops, Player player, Material material) {
        if (material.isBlock() && Antiglitch.isStoreBrokenBlocks()) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent storing broken block.");

            final List<ItemStack> itemStacks = drops.stream()
                    .map(Item::getItemStack)
                    .toList();

            storeBrokenBlockMetadata(itemStacks, player);
        }
    }
}
