package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.UseCustomFurnaceResults;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.inventory.ItemStack;

public class FurnaceExtractListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onFurnaceExtractEvent(FurnaceExtractEvent event) {
        if (UseCustomFurnaceResults.isEnabled()) return;
        setPlayerQuestProgression(event.getPlayer(), new ItemStack(event.getItemType()), event.getItemAmount(), QuestType.COOK);
    }
}

