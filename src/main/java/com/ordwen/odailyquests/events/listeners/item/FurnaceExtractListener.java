package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.ItemStack;

public class FurnaceExtractListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        setPlayerQuestProgression(event.getPlayer(), new ItemStack(event.getItemType()), event.getItemAmount(), QuestType.COOK, null);
    }
}

