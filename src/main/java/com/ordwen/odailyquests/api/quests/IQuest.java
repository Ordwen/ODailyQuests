package com.ordwen.odailyquests.api.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;

public interface IQuest {

    String getType();
    boolean canProgress(Event provided);
    boolean loadParameters(ConfigurationSection section, String file, String index);
}
