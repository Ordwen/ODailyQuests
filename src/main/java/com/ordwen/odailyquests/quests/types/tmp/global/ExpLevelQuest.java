package com.ordwen.odailyquests.quests.types.tmp.global;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class ExpLevelQuest extends AbstractQuest {

    public ExpLevelQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "EXP_LEVELS";
    }

    @Override
    public boolean canProgress(Event provided) {
        return provided instanceof PlayerLevelChangeEvent;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        return true;
    }
}
