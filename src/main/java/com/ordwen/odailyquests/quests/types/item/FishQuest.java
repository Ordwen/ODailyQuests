package com.ordwen.odailyquests.quests.types.item;

import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import com.ordwen.odailyquests.quests.types.shared.ItemQuest;
import net.momirealms.customfishing.api.event.FishingLootSpawnEvent;
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

        if (provided instanceof FishingLootSpawnEvent event) {
            if (event.getEntity() instanceof Item item) {
                return super.isRequiredItem(item.getItemStack());
            } else {
                return false;
            }
        }

        return false;
    }
}
