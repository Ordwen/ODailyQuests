package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockPlaceListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        boolean valid = true;

        if (Antiglitch.isStoreBrokenBlocks()) {
            final ItemStack placedItem = player.getInventory().getItemInMainHand();
            final ItemMeta placedItemMeta = placedItem.getItemMeta();

            if (placedItemMeta != null) {
                final PersistentDataContainer pdc = placedItemMeta.getPersistentDataContainer();
                final String placedItemKey = pdc.get(Antiglitch.BROKEN_KEY, PersistentDataType.STRING);

                if (placedItemKey != null && placedItemKey.equals(player.getUniqueId().toString())) {
                    valid = false;
                }
            }
        }

        if (valid) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("BlockPlaceListener: onBlockPlaceEvent summoned by " + player.getName() + " for " + block.getType() + ".");

            setPlayerQuestProgression(player, new ItemStack(block.getType()), 1, "PLACE");
        }

        if (Antiglitch.isStorePlacedBlocks()) {
            block.setMetadata("odailyquests:placed", new FixedMetadataValue(ODailyQuests.INSTANCE, player.getUniqueId().toString()));
        }
    }
}

