package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
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

public class BlockDropItemListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final BlockData data = event.getBlockState().getBlockData();
        final Material dataMaterial = data.getMaterial();

        final List<Item> drops = event.getItems();
        if (drops.isEmpty()) return;

        // check if the dropped item is a crop
        if (data instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge()) {
                handleDrops(player, drops);
                return;
            }
        }

        // check if the block have been placed by the player
        if (dataMaterial.isBlock()) {
            if (Antiglitch.isStorePlacedBlocks()) {
                if (!event.getBlock().getMetadata("odailyquests:placed").isEmpty()) {
                    return;
                }
            }
        }

        handleDrops(player, drops);

        // check if the dropped item is a block that can be posed
        if (dataMaterial.isBlock()) {
            if (Antiglitch.isStoreBrokenBlocks()) {

                for (Item item : event.getItems()) {
                    final ItemStack drop = item.getItemStack();
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
     * @param player involved player in the event
     * @param drops  list of dropped items
     */
    private void handleDrops(Player player, List<Item> drops) {
        for (Item item : drops) {
            final ItemStack droppedItem = item.getItemStack();
            final Material droppedMaterial = droppedItem.getType();

            setPlayerQuestProgression(player, new ItemStack(droppedMaterial), droppedItem.getAmount(), QuestType.FARMING);
        }
    }
}
