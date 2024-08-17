package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

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
                final var block = event.getBlocks().get(i);
                if (!block.getMetadata("odailyquests:placed").isEmpty()) {
                    Debugger.addDebug("StructureGrowListener: block at coordinates " + block.getX() + ", " + block.getY() + ", " + block.getZ() + " is a placed block. Removing metadataKey.");
                    block.removeMetadata("odailyquests:placed", ODailyQuests.INSTANCE);
                }
            }
            Debugger.addDebug("StructureGrowListener: onStructureGrowEvent placed block check complete.");
        }
    }
}
