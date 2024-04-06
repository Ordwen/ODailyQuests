package com.ordwen.odailyquests.quests.types.tmp.global;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;

public class FireballReflectQuest extends AbstractQuest {

    public FireballReflectQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "FIREBALL_REFLECT";
    }

    @Override
    public boolean canProgress(Event provided) {
        return provided instanceof ProjectileHitEvent;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        return true;
    }
}
