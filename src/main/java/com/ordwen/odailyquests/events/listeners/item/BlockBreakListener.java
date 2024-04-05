package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class BlockBreakListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {

        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (ItemsAdderEnabled.isEnabled()) {
            final CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
            if (customBlock != null) return;
        }

        final AtomicBoolean valid = new AtomicBoolean(true);

        if (Antiglitch.isStorePlacedBlocks()) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() != ageable.getMaximumAge()) {
                    valid.set(false);
                }
            }

            else {
                if (!block.getMetadata("odailyquests:placed").isEmpty()) {
                    valid.set(false);
                }
            }
        }

        if (valid.get()) {

            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("BlockBreakListener: onBlockBreakEvent summoned by " + player.getName() + " for " + block.getType() + ".");

            setPlayerQuestProgression(event, player, 1, "BREAK");
        }
    }
}



