package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicBoolean;

public class BlockBreakListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        AtomicBoolean valid = new AtomicBoolean(true);

        if (Antiglitch.storeItems) {
            block.getMetadata("odailyquests:placed").forEach(metadataValue -> {
                if (metadataValue.asString().equals(player.getUniqueId().toString())) {
                    valid.set(false);
                }
            });
        }

        if (valid.get()) setPlayerQuestProgression(player, new ItemStack(block.getType()), 1, QuestType.BREAK, block.getBlockData().getAsString());
    }
}



