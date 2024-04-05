package com.ordwen.odailyquests.quests.types.tmp;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public class FireballReflectQuest extends AbstractQuest {

    public FireballReflectQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "FIREBALL_REFLECT";
    }

    @Override
    public boolean canProgress(Event event) {
        return true;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        return true;
    }
}
