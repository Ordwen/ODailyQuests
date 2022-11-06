package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        final Block block = event.getBlock();
        setPlayerQuestProgression(event.getPlayer(), new ItemStack(block.getType()), 1, QuestType.BREAK, block.getBlockData().getAsString());
    }
}
