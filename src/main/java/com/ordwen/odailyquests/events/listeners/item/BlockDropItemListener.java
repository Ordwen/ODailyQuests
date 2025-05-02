package com.ordwen.odailyquests.events.listeners.item;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import com.ordwen.odailyquests.quests.types.item.FarmingQuest;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

        Debugger.write("BlockDropItemListener: onBlockDropItemEvent block data: " + dataMaterial.name() + ".");

        final List<Item> drops = event.getItems();
        if (drops.isEmpty()) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent no drops.");
            return;
        }

        // check if the dropped item is a crop
        if (data instanceof Ageable ageable) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent ageable block: " + dataMaterial + ".");

            if (ageable.getAge() == ageable.getMaximumAge()) {
                Debugger.write("BlockDropItemListener: onBlockDropItemEvent ageable block is mature.");
                handleDrops(event, player, drops);

                return;
            }
        }

        // check if the block have been placed by the player
        if (isPlayerPlacedBlock(event, dataMaterial)) return;

        handleDrops(event, player, drops);

        // check if the dropped item is a block that can be posed
        handleStoreBrokenBlocks(dataMaterial, event);
    }

    /**
     * Check if the block has been placed by the player.
     *
     * @param event    the event that triggered the listener
     * @param material the material of the block
     * @return true if the block has been placed by the player
     */
    private boolean isPlayerPlacedBlock(BlockDropItemEvent event, Material material) {
        if (material.isBlock() && Antiglitch.isStorePlacedBlocks()) {
            final PersistentDataContainer pdc = new CustomBlockData(event.getBlock(), ODailyQuests.INSTANCE);
            // check if type has changed
            final String previousType = pdc.getOrDefault(Antiglitch.PLACED_KEY, PersistentDataType.STRING, material.name());
            if (previousType.equals(material.name())) {
                Debugger.write("BlockDropItemListener: onBlockDropItemEvent cancelled due to placed block.");
                return true;
            } else {
                Debugger.write("BlockDropItemListener: onBlockDropItemEvent block type has changed (" + previousType + " -> " + material.name() + ").");
            }
        }
        return false;
    }

    /**
     * Store the broken blocks by the player by adding a unique identifier to the dropped item.
     *
     * @param event the event that triggered the listener
     */
    private void handleStoreBrokenBlocks(Material material, BlockDropItemEvent event) {
        if (material.isBlock() && Antiglitch.isStoreBrokenBlocks()) {
            Debugger.write("BlockDropItemListener: onBlockDropItemEvent storing broken block.");
            for (Item item : event.getItems()) {
                final ItemStack drop = item.getItemStack();
                Debugger.write("BlockDropItemListener: onBlockDropItemEvent storing broken block: " + drop.getType());
                final ItemMeta dropMeta = drop.getItemMeta();
                if (dropMeta == null) continue;

                final PersistentDataContainer pdc = dropMeta.getPersistentDataContainer();
                pdc.set(Antiglitch.BROKEN_KEY, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
                drop.setItemMeta(dropMeta);
            }
        }
    }

    /**
     * Handle the dropped items and update the player progression.
     *
     * @param event  the event that triggered the listener
     * @param player involved player in the event
     * @param drops  list of dropped items
     */
    private void handleDrops(BlockDropItemEvent event, Player player, List<Item> drops) {
        Debugger.write("BlockDropItemListener: handleDrops summoned.");
        for (Item item : drops) {
            final ItemStack droppedItem = item.getItemStack();
            final Material droppedMaterial = droppedItem.getType();
            Debugger.write("BlockDropItemListener: handling drop: " + droppedMaterial + ".");

            FarmingQuest.setCurrent(new ItemStack(droppedMaterial));
            setPlayerQuestProgression(event, player, droppedItem.getAmount(), "FARMING");
        }
    }
}
