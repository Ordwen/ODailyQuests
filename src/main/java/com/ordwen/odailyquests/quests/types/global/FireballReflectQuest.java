package com.ordwen.odailyquests.quests.types.global;

import com.ordwen.odailyquests.quests.player.progression.Progression;
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
    public boolean canProgress(Event provided, Progression progression) {
        return provided instanceof ProjectileHitEvent;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        return true;
    }
}
