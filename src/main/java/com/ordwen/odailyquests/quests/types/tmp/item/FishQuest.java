package com.ordwen.odailyquests.quests.types.tmp.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import org.bukkit.entity.Item;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerFishEvent;

public class FishQuest extends ItemQuest {

    public FishQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "FISH";
    }

    @Override
    public boolean canProgress(Event provided) {
        if (provided instanceof PlayerFishEvent event) {
            final Item item = (Item) event.getCaught();
            if (item == null) return false;
            return super.isRequiredItem(item.getItemStack());
        }

        return false;
    }
}
