package com.ordwen.odailyquests.quests.player.progression.listeners;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.api.events.QuestProgressEvent;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressionMessage;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestProgressListener implements Listener {

    @EventHandler
    public void onQuestProgressEvent(QuestProgressEvent event) {
        final Player player = event.getPlayer();
        final Progression progression = event.getProgression();
        final AbstractQuest quest = event.getQuest();
        final int amount = event.getAmount();

        if (Utils.isTimeToRenew(player, QuestsManager.getActiveQuests())) return;

        if (!quest.getRequiredWorlds().isEmpty() && !quest.getRequiredWorlds().contains(player.getWorld().getName())) {
            final String msg = QuestsMessages.NOT_REQUIRED_WORLD.getMessage(player);
            if (msg != null) player.sendMessage(msg);

            return;
        }

        for (int i = 0; i < amount; i++) {
            progression.increaseProgression();
        }

        if (progression.getProgression() >= quest.getAmountRequired()) {
            final QuestCompletedEvent completedEvent = new QuestCompletedEvent(player, progression, quest);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(completedEvent);
            return;
        }

        ProgressionMessage.sendProgressionMessage(player, quest.getQuestName(), progression.getProgression(), quest.getAmountRequired());
    }
}
