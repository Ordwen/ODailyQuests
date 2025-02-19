package com.ordwen.odailyquests.events.listeners.item;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.externs.hooks.items.KGeneratorsHook;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class BlockBreakListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {

        Debugger.write("BlockBreakListener: onBlockBreakEvent summoned.");
        if (event.isCancelled()) {
            Debugger.write("BlockBreakListener: onBlockBreakEvent cancelled.");
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (ItemsAdderEnabled.isEnabled()) {
            final CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
            if (customBlock != null) {
                Debugger.write("BlockBreakListener: onBlockBreakEvent cancelled due to ItemsAdder custom block.");
                return;
            }
        }

        boolean valid = canProgress(block);

        if (valid) {
            Debugger.write("BlockBreakListener: onBlockBreakEvent summoned by " + player.getName() + " for " + block.getType() + ".");
            setPlayerQuestProgression(event, player, 1, "BREAK");
        }
    }

    /**
     * Check if the block have been placed by the player.
     * If so, check if the block is a crop and if it is mature, or if the block come from a generator.
     *
     * @param block the block that is being broken
     * @return true if th quest progression can continue, false otherwise
     */
    private static boolean canProgress(Block block) {
        if (Antiglitch.isStorePlacedBlocks()) {
            Debugger.write("BlockBreakListener: onBlockBreakEvent checking for placed blocks.");
            if (block.getBlockData() instanceof Ageable ageable && ageable.getAge() != ageable.getMaximumAge()) {
                Debugger.write("BlockBreakListener: onBlockBreakEvent cancelled due to ageable block.");
                return false;
            }

            final PersistentDataContainer pdc = new CustomBlockData(block, ODailyQuests.INSTANCE);
            if (pdc.has(Antiglitch.PLACED_KEY)) {
                if (KGeneratorsHook.isKGeneratorsLocation(block.getLocation())) {
                    Debugger.write("BlockBreakListener: onBlockBreakEvent processing KGenerators generator.");
                } else {
                    Debugger.write("BlockBreakListener: onBlockBreakEvent cancelled due to placed block.");
                    return false;
                }
            }
            Debugger.write("BlockBreakListener: onBlockBreakEvent placed block check complete.");
        }

        return true;
    }
}



