package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.events.listeners.inventory.types.AbstractQuest;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.entity.Player;

public abstract class AbstractProgressionIncreaser {

    public void increaseProgression(Player player, Progression progression, AbstractQuest quest, int amount) {

        for (int i = 0; i < amount; i++) {
            progression.increaseProgression();
        }

        if (progression.getProgression() >= quest.getAmountRequired()) {
            progression.setAchieved();
            QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());
            RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());
        }
    }
}
