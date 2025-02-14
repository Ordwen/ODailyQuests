package com.ordwen.odailyquests.events.listeners.integrations.itemsadder;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomBlockBreakListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onCustomBlockBreakEvent(CustomBlockBreakEvent event) {
        Debugger.addDebug("CustomBlockBreakListener: onCustomBlockBreakEvent summoned.");

        if (event.isCancelled()) {
            Debugger.addDebug("CustomBlockBreakListener: onCustomBlockBreakEvent is cancelled.");
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        AtomicBoolean valid = new AtomicBoolean(true);

        if (Antiglitch.isStorePlacedBlocks()) {
            final PersistentDataContainer pdc = new CustomBlockData(block, ODailyQuests.INSTANCE);
            if (pdc.has(Antiglitch.PLACED_KEY)) {
                valid.set(false);
            }
        }

        if (valid.get()) {
            setPlayerQuestProgression(event, player, 1, "BREAK");
        }
    }
}
