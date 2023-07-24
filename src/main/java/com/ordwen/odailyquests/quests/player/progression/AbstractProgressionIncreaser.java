package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.api.events.QuestCompletedEvent;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.entity.Player;

public abstract class AbstractProgressionIncreaser {

    public void increaseProgression(Player player, Progression progression, AbstractQuest quest, int amount) {

        if (!quest.getRequiredWorlds().isEmpty() && !quest.getRequiredWorlds().contains(player.getWorld().getName())) {

            //PluginLogger.warn("World " + player.getWorld().getName() + " is not in the required worlds list for quest " + quest.getQuestName());

            return;
        }

        for (int i = 0; i < amount; i++) {
            progression.increaseProgression();
        }

        if (progression.getProgression() >= quest.getAmountRequired()) {
            final QuestCompletedEvent event = new QuestCompletedEvent(player, progression, quest);
            ODailyQuests.INSTANCE.getServer().getPluginManager().callEvent(event);
        }

        //PluginLogger.warn("Progression for quest " + quest.getQuestName() + " is now " + progression.getProgression());
    }
}
