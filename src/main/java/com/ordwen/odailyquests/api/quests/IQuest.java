package com.ordwen.odailyquests.api.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public interface IQuest {

    boolean canProgress(Event event);
    void loadParameters(ConfigurationSection section, String file, int index);
}
