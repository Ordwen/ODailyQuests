package com.ordwen.odailyquests.quests.types;

import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.rewards.Reward;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GlobalQuest extends AbstractQuest {

    public GlobalQuest(int questIndex, String questName, List<String> questDesc, QuestType questType, ItemStack menuItem, ItemStack achievedItem, int amountRequired, Reward reward, List<String> requiredWorlds, boolean isUsingPlaceholders) {
        super(questIndex, questName, questDesc, questType, menuItem, achievedItem, amountRequired, reward, requiredWorlds, isUsingPlaceholders);
    }
}
