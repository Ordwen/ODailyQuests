package com.ordwen.odailyquests.quests.types.tmp.item;

import com.ordwen.odailyquests.events.listeners.item.BlockDropItemListener;
import com.ordwen.odailyquests.events.listeners.item.PlayerHarvestBlockListener;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

public class FarmingQuest extends ItemQuest {

    public FarmingQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "FARMING";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof PlayerHarvestBlockEvent) {
            return super.isRequiredItem(PlayerHarvestBlockListener.current);
        }

        if (provided instanceof BlockDropItemEvent) {
            return super.isRequiredItem(BlockDropItemListener.current);
        }

        return false;
    }
}