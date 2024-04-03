package com.ordwen.odailyquests.api.quests;

import org.bukkit.configuration.ConfigurationSection;

public interface IQuest {

    boolean canProgress();
    void loadParameters(ConfigurationSection section, String file, int index);
}
