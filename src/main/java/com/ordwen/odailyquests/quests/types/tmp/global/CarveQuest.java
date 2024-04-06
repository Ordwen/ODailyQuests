package com.ordwen.odailyquests.quests.types.tmp.global;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;

public class CarveQuest extends AbstractQuest {

    public CarveQuest(BasicQuest base) {
        super(base);
    }
    @Override
    public String getType() {
        return "CARVE";
    }

    @Override
    public boolean canProgress(Event provided) {
        return provided instanceof PlayerInteractEvent;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        return true;
    }
}
