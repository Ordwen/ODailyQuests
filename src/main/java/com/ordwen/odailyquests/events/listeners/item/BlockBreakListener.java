package com.ordwen.odailyquests.events.listeners.item;

import com.jeff_media.customblockdata.CustomBlockData;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.externs.hooks.items.KGeneratorsHook;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Set;

public class BlockBreakListener extends PlayerProgressor implements Listener {

    private static final Set<Material> VERTICAL_PLANTS_UP = Set.of(
            Material.SUGAR_CANE,
            Material.CACTUS,
            Material.BAMBOO,
            Material.KELP_PLANT,
            Material.TWISTING_VINES_PLANT
    );

    private static final Set<Material> VERTICAL_PLANTS_DOWN = Set.of(
            Material.WEEPING_VINES_PLANT,
            Material.CAVE_VINES_PLANT
    );

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Debugger.write("BlockBreakListener: onBlockBreakEvent summoned.");
        if (event.isCancelled()) {
            Debugger.write("BlockBreakListener: onBlockBreakEvent cancelled.");
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final Material material = block.getType();

        Debugger.write("BlockBreakListener: onBlockBreakEvent block type: " + material.name() + ".");

        if (VERTICAL_PLANTS_UP.contains(material)) {
            Debugger.write("BlockBreakListener: onBlockBreakEvent vertical plant detected (UP).");
            handleVerticalPlant(event, material, BlockFace.UP);
            return;
        }

        if (VERTICAL_PLANTS_DOWN.contains(material)) {
            Debugger.write("BlockBreakListener: onBlockBreakEvent vertical plant detected (DOWN).");
            handleVerticalPlant(event, material, BlockFace.DOWN);
            return;
        }

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
    private boolean canProgress(Block block) {
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

    /**
     * Handles FARMING quest progression for vertical plants (such as sugar cane, bamboo, or cactus).
     * <p>
     * This method counts how many vertically stacked blocks of the same type exist starting from
     * the broken block, and triggers the corresponding quest progression.
     * <p>
     * It addresses the behavior in Minecraft where breaking the bottom block of a vertical plant causes
     * the rest to break or fall without triggering separate events for the upper blocks.
     *
     * @param event     the block break event triggered by the player
     * @param plantType the type of vertical plant being handled (e.g., SUGAR_CANE, BAMBOO, CACTUS)
     * @param blockFace the direction to check for vertical plant blocks (UP or DOWN)
     */
    private void handleVerticalPlant(BlockBreakEvent event, Material plantType, BlockFace blockFace) {
        Block base = event.getBlock();
        int count = 0;

        while (base.getType() == plantType) {
            count++;
            base = base.getRelative(blockFace);
        }

        Debugger.write("BlockBreakListener: handleVerticalPlant found " + count + " vertical plant blocks.");

        if (count > 0) {
            if (Antiglitch.isStorePlacedBlocks() && count < 2) {
                Debugger.write("BlockBreakListener: handleVerticalPlant cancelled due to single vertical plant block.");
                return;
            }

            setPlayerQuestProgression(event, event.getPlayer(), count, "FARMING");
        }
    }
}



