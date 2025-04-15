package com.ordwen.odailyquests.api.quests;

import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public interface IQuest {

    String getType();
    boolean canProgress(@Nullable Event provided, Progression progression);
    boolean loadParameters(ConfigurationSection section, String file, String index);
}
