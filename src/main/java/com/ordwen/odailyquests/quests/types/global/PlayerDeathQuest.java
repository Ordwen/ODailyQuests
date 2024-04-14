package com.ordwen.odailyquests.quests.types.global;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.shared.BasicQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathQuest extends AbstractQuest {

    public PlayerDeathQuest(BasicQuest base) {
        super(base);
    }

    @Override
    public String getType() {
        return "PLAYER_DEATH";
    }

    @Override
    public boolean canProgress(Event provided) {
        return provided instanceof PlayerRespawnEvent;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        return true;
    }
}
