package com.ordwen.odailyquests.quests.types.item;

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
            System.out.println("caught item: " + item);
            if (item == null) return false;
            System.out.println("caught itemstack: " + item.getItemStack());
            return super.isRequiredItem(item.getItemStack());
        }

        return false;
    }
}
