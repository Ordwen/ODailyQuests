package com.ordwen.odailyquests.events.listeners.item;

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
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BlockDropItemListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent summoned.");

        if (event.isCancelled()) {
            Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent is cancelled.");
            return;
        }

        final Player player = event.getPlayer();
        final BlockData data = event.getBlockState().getBlockData();
        final Material dataMaterial = data.getMaterial();

        final List<Item> drops = event.getItems();
        if (drops.isEmpty()) {
            Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent no drops.");
            return;
        }

        // check if the dropped item is a crop
        if (data instanceof Ageable ageable) {
            Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent ageable block: " + dataMaterial + ".");
            if (ageable.getAge() == ageable.getMaximumAge()) {
                Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent ageable block is mature.");
                handleDrops(event, player, drops);

                return;
            }
        }

        // check if the block have been placed by the player
        if (dataMaterial.isBlock()) {
            if (Antiglitch.isStorePlacedBlocks()) {
                if (!event.getBlock().getMetadata("odailyquests:placed").isEmpty()) {
                    Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent cancelled due to placed block.");
                    return;
                }
            }
        }

        //handleDrops(event, player, drops); ????

        // check if the dropped item is a block that can be posed
        if (dataMaterial.isBlock()) {
            if (Antiglitch.isStoreBrokenBlocks()) {
                Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent storing broken block.");
                for (Item item : event.getItems()) {
                    final ItemStack drop = item.getItemStack();
                    Debugger.addDebug("BlockDropItemListener: onBlockDropItemEvent storing broken block: " + drop.getType());
                    final ItemMeta dropMeta = drop.getItemMeta();
                    if (dropMeta == null) continue;

                    final PersistentDataContainer pdc = dropMeta.getPersistentDataContainer();
                    pdc.set(Antiglitch.BROKEN_KEY, PersistentDataType.STRING, event.getPlayer().getUniqueId().toString());
                    drop.setItemMeta(dropMeta);
                }
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
        Debugger.addDebug("BlockDropItemListener: handleDrops summoned.");
        for (Item item : drops) {
            Debugger.addDebug("BlockDropItemListener: handling drop: " + item.getItemStack().getType() + ".");
            final ItemStack droppedItem = item.getItemStack();
            final Material droppedMaterial = droppedItem.getType();

            FarmingQuest.setCurrent(new ItemStack(droppedMaterial));
            setPlayerQuestProgression(event, player, droppedItem.getAmount(), "FARMING");
        }
    }
}
