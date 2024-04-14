package com.ordwen.odailyquests.api.quests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public interface IQuest {

    String getType();
    boolean canProgress(Event provided);
    boolean loadParameters(ConfigurationSection section, String file, int index);
}
