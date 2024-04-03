package com.ordwen.odailyquests.api.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public interface IQuest {

    String getType();
    boolean canProgress(Event event);
    boolean loadParameters(ConfigurationSection section, String file, int index);
}
