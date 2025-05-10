package com.ordwen.odailyquests.events.listeners.item;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockPlaceListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        boolean valid = true;

        if (Antiglitch.isStoreBrokenBlocks()) {
            Debugger.write("BlockPlaceListener: onBlockPlaceEvent checking for broken blocks.");
            final ItemStack placedItem = player.getInventory().getItemInMainHand();
            final ItemMeta placedItemMeta = placedItem.getItemMeta();

            if (placedItemMeta != null) {
                final PersistentDataContainer pdc = placedItemMeta.getPersistentDataContainer();
                final String placedItemKey = pdc.get(Antiglitch.BROKEN_KEY, PersistentDataType.STRING);

                if (placedItemKey != null && placedItemKey.equals(player.getUniqueId().toString())) {
                    Debugger.write("BlockPlaceListener: onBlockPlaceEvent cancelled due to broken block.");
                    valid = false;
                }
            }
            Debugger.write("BlockPlaceListener: onBlockPlaceEvent broken block check complete.");
        }

        if (valid) {
            Debugger.write("BlockPlaceListener: onBlockPlaceEvent summoned by " + player.getName() + " for " + block.getType() + ".");
            setPlayerQuestProgression(event, player, 1, "PLACE");
        }

        if (Antiglitch.isStorePlacedBlocks()) {
            Debugger.write("BlockPlaceListener: onBlockPlaceEvent storing placed block.");
            final PersistentDataContainer pdc = new CustomBlockData(block, ODailyQuests.INSTANCE);
            System.out.println(block.getType().name());
            pdc.set(Antiglitch.PLACED_KEY, PersistentDataType.STRING, block.getType().name());
            System.out.println(pdc.has(Antiglitch.PLACED_KEY, PersistentDataType.STRING));
            System.out.println(pdc.get(Antiglitch.PLACED_KEY, PersistentDataType.STRING));
            test = block;
            BlockPlaceListener.pdc = pdc;
        }
    }

    public static Block test;
    public static PersistentDataContainer pdc;
}

