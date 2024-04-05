package com.ordwen.odailyquests.quests.types;

import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BasicQuest extends AbstractQuest {

    public BasicQuest(int questIndex, String questName, String categoryName, List<String> questDesc, String questType, ItemStack menuItem, ItemStack achievedItem, int amountRequired, Reward reward, List<String> requiredWorlds, boolean isUsingPlaceholders) {
        super(questIndex, questName, categoryName, questDesc, questType, menuItem, achievedItem, amountRequired, reward, requiredWorlds, isUsingPlaceholders);
    }

    @Override
    public String getType() {
        return "BASIC";
    }

    @Override
    public boolean canProgress(Event event) {
        return false;
    }

    @Override
    public boolean loadParameters(ConfigurationSection section, String file, int index) {
        return true;
    }
}
