package com.ordwen.odailyquests.quests.types.global;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class MilkingQuest extends AbstractQuest {

    public MilkingQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "MILKING";
    }

    @Override
    public boolean canProgress(Event provided) {
        return provided instanceof PlayerBucketFillEvent;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        return true;
    }
}
