package com.ordwen.odailyquests.quests.types.shared;

import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BasicQuest extends AbstractQuest {

    public BasicQuest(int questIndex, String questName, String categoryName, List<String> questDesc, String questType, ItemStack menuItem, ItemStack achievedItem, int amountRequired, Reward reward, List<String> requiredWorlds, List<String> requiredRegions, boolean protectionBypass, String requiredPermission) {
        super(questIndex, questName, categoryName, questDesc, questType, menuItem, achievedItem, amountRequired, reward, requiredWorlds, requiredRegions, protectionBypass, requiredPermission);
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
    public boolean loadParameters(ConfigurationSection section, String file, String index) {
        return true;
    }
}
