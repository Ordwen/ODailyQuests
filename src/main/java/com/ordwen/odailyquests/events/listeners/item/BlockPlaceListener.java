package com.ordwen.odailyquests.events.listeners.item;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.events.antiglitch.BrokenBlocksAntiglitch;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Handles PLACE quests progression and anti-glitch checks for block placement.
 * <p>
 * This listener supports two independent anti-glitch mechanisms:
 * <ul>
 *   <li><b>Break -> Place</b> protection (PLACE quests):
 *       blocks obtained from a recent break by the same player are ignored for PLACE progression
 *       (implemented via {@link BrokenBlocksAntiglitch}, without modifying ItemStacks).</li>
 *   <li><b>Placed blocks tracking</b> (BREAK quests):
 *       blocks placed by players are marked in block PDC so they can be excluded from BREAK progression
 *       (using {@link CustomBlockData}).</li>
 * </ul>
 * </p>
 */
public class BlockPlaceListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockPlaced(); // more explicit than getBlock()
        final Material placedType = block.getType();

        boolean canProgress = true;

        // Anti-glitch: prevent "break -> place" abuse for PLACE quests
        if (Antiglitch.isStoreBrokenBlocks()) {
            Debugger.write("BlockPlaceListener: checking break->place anti-glitch.");

            if (BrokenBlocksAntiglitch.shouldCancelPlaceProgress(player, placedType)) {
                Debugger.write("BlockPlaceListener: PLACE progression cancelled due to recently broken block.");
                canProgress = false;
            }

            Debugger.write("BlockPlaceListener: break->place anti-glitch check complete.");
        }

        if (canProgress) {
            Debugger.write("BlockPlaceListener: progression for " + player.getName() + " placing " + placedType + ".");
            setPlayerQuestProgression(event, player, 1, "PLACE");
        }

        // Anti-glitch: mark placed blocks so they don't count for BREAK quests
        if (Antiglitch.isStorePlacedBlocks()) {
            Debugger.write("BlockPlaceListener: storing placed block location.");
            final PersistentDataContainer pdc = new CustomBlockData(block, ODailyQuests.INSTANCE);
            pdc.set(Antiglitch.PLACED_KEY, PersistentDataType.STRING, placedType.name());
        }
    }
}
