package com.ordwen.odailyquests.events.listeners.inventory.types;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GlobalQuest extends AbstractQuest {

    public GlobalQuest(int questIndex, String questName, List<String> questDesc, QuestType questType, ItemStack menuItem, int amountRequired, Reward reward) {
        super(questIndex, questName, questDesc, questType, menuItem, amountRequired, reward);
    }
}
