package com.ordwen.odailyquests.quests.player.progression;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerProgressor {

    /**
     * Set the player's progression for a specific quest type
     *
     * @param player    the player to set the progression for
     * @param amount    the amount to set the progression to
     * @param questType the quest type to set the progression for
     */
    public void setPlayerQuestProgression(Player player, int amount, String questType) {

        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            return;
        }

        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {
            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();
            for (AbstractQuest abstractQuest : playerQuests.keySet()) {
                if (abstractQuest.getQuestType().equals(questType)) {
                    final Progression progression = playerQuests.get(abstractQuest);
                    if (!progression.isAchieved()) {
                        QuestProgressUtils.actionQuest(player, progression, abstractQuest, amount);
                        if (!Synchronization.isSynchronised()) break;
                    }
                }
            }
        }
    }
}
