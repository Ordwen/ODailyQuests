package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
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

public class BlockPlaceListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        boolean valid = true;

        if (Antiglitch.isStoreBrokenBlocks()) {
            Debugger.addDebug("BlockPlaceListener: onBlockPlaceEvent checking for broken blocks.");
            final ItemStack placedItem = player.getInventory().getItemInMainHand();
            final ItemMeta placedItemMeta = placedItem.getItemMeta();

            if (placedItemMeta != null) {
                final PersistentDataContainer pdc = placedItemMeta.getPersistentDataContainer();
                final String placedItemKey = pdc.get(Antiglitch.BROKEN_KEY, PersistentDataType.STRING);

                if (placedItemKey != null && placedItemKey.equals(player.getUniqueId().toString())) {
                    Debugger.addDebug("BlockPlaceListener: onBlockPlaceEvent cancelled due to broken block.");
                    valid = false;
                }
            }
            Debugger.addDebug("BlockPlaceListener: onBlockPlaceEvent broken block check complete.");
        }

        if (valid) {
            Debugger.addDebug("BlockPlaceListener: onBlockPlaceEvent summoned by " + player.getName() + " for " + block.getType() + ".");
            setPlayerQuestProgression(event, player, 1, "PLACE");
        }

        if (Antiglitch.isStorePlacedBlocks()) {
            Debugger.addDebug("BlockPlaceListener: onBlockPlaceEvent storing placed block.");
            block.setMetadata("odailyquests:placed", new FixedMetadataValue(ODailyQuests.INSTANCE, player.getUniqueId().toString()));
        }
    }
}

