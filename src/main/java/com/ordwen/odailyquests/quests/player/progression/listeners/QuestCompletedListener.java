package com.ordwen.odailyquests.quests.player.progression.listeners;

import com.ordwen.odailyquests.externs.hooks.placeholders.PAPIHook;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestCompletedListener implements Listener {

    @EventHandler
    public void onQuestCompletedEvent(QuestCompletedEvent event) {
        final var player = event.getPlayer();
        final var progression = event.getProgression();
        final var quest = event.getAbstractQuest();

        progression.setAchieved();
        QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player);
        RewardManager.sendAllRewardItems(PAPIHook.getPlaceholders(player, quest.getQuestName()), player, quest.getReward());
    }
}
