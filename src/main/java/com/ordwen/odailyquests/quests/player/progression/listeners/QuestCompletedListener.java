package com.ordwen.odailyquests.quests.player.progression.listeners;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.rewards.RewardManager;
import com.ordwen.odailyquests.tools.QuestPlaceholders;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestCompletedListener implements Listener {

    @EventHandler
    public void onQuestCompletedEvent(QuestCompletedEvent event) {
        final var player = event.getPlayer();
        final var progression = event.getProgression();
        final var quest = event.getAbstractQuest();

        Debugger.write("QuestCompletedListener: QuestCompletedEvent summoned by " + player.getName() + " for " + quest.getQuestName() + ".");

        /* prevention of excess progressions when mobs are killed with the sweeping edge enchantment */
        if (progression.isAchieved()) return;

        progression.setAchieved();

        final String formattedQuestName = QuestPlaceholders.replaceQuestPlaceholders(
                TextFormatter.format(player, quest.getQuestName()),
                player,
                quest,
                progression,
                null,
                null
        );

        RewardManager.sendQuestRewardItems(formattedQuestName, player, quest.getReward(), progression);
        ODailyQuestsAPI.getPlayerQuests(player.getName()).increaseCategoryAchievedQuests(quest.getCategoryName(), player);
    }
}
