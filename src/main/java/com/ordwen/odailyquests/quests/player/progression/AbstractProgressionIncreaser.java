package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.configuration.functionalities.progression.ProgressionMessage;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.entity.Player;

public abstract class AbstractProgressionIncreaser {

    public void increaseProgression(Player player, Progression progression, AbstractQuest quest, int amount) {

        if (!quest.getRequiredWorlds().isEmpty() && !quest.getRequiredWorlds().contains(player.getWorld().getName())) {
            return;
        }

        for (int i = 0; i < amount; i++) {
            progression.increaseProgression();
        }

        if (progression.getProgression() >= quest.getAmountRequired()) {
            final QuestCompletedEvent event = new QuestCompletedEvent(player, progression, quest);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
            return;
        }

        ProgressionMessage.sendProgressionMessage(player, quest.getQuestName(), progression.getProgression(), quest.getAmountRequired());
    }
}
