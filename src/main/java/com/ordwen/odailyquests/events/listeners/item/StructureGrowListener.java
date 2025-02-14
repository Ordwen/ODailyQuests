package com.ordwen.odailyquests.events.listeners.item;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class StructureGrowListener implements Listener {

    @EventHandler
    public void onStructureGrowEvent(StructureGrowEvent event) {
        Debugger.addDebug("StructureGrowListener: onStructureGrowEvent summoned.");

        if (event.isCancelled()) {
            return;
        }

        if (Antiglitch.isStorePlacedBlocks()) {
            Debugger.addDebug("StructureGrowListener: onStructureGrowEvent checking for placed blocks.");
            for (int i = 0; i < event.getBlocks().size(); i++) {
                final Block block = event.getBlocks().get(i).getBlock();
                final PersistentDataContainer pdc = new CustomBlockData(block, ODailyQuests.INSTANCE);
                if (pdc.has(Antiglitch.PLACED_KEY)) {
                    Debugger.addDebug("StructureGrowListener: block at coordinates " + block.getX() + ", " + block.getY() + ", " + block.getZ() + " is a placed block. Removing metadataKey.");
                    block.removeMetadata("odailyquests:placed", ODailyQuests.INSTANCE);
                }
            }
            Debugger.addDebug("StructureGrowListener: onStructureGrowEvent placed block check complete.");
        }
    }
}
