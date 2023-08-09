package com.ordwen.odailyquests.events.listeners.integrations.itemsadder;

import com.ordwen.odailyquests.configuration.essentials.Antiglitch;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomBlockBreakListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onCustomBlockBreakEvent(CustomBlockBreakEvent event) {
        if (event.isCancelled()) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        AtomicBoolean valid = new AtomicBoolean(true);

        if (Antiglitch.isStorePlacedBlocks()) {
            block.getMetadata("odailyquests:placed").forEach(metadataValue -> {
                if (metadataValue.asString().equals(player.getUniqueId().toString())) {
                    valid.set(false);
                }
            });
        }

        if (valid.get()) {
            setPlayerQuestProgression(player, event.getCustomBlockItem(), 1, QuestType.BREAK, block.getBlockData().getAsString());
        }
    }
}
