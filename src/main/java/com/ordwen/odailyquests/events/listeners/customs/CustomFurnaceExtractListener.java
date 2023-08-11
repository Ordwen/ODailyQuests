package com.ordwen.odailyquests.events.listeners.customs;

import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.events.customs.CustomFurnaceExtractEvent;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CustomFurnaceExtractListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onCustomFurnaceExtractEvent(CustomFurnaceExtractEvent event) {
        final ItemStack result = event.getResult();
        setPlayerQuestProgression(event.getPlayer(), result, result.getAmount(), QuestType.COOK, null);
    }
}
