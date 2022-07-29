package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.types.ItemQuest;
import com.ordwen.odailyquests.quests.player.progression.types.LocationQuest;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AbstractPositionChecker {

    /**
     * Increase player quest progression.
     *
     * @param player    the player to increase progression for.
     * @param questType the quest type to increase progression for.
     */
    public void setPlayerQuestProgression(Player player, QuestType questType) {
        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            return;
        }

        final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();

        for (AbstractQuest abstractQuest : playerQuests.keySet()) {

            final Progression progression = playerQuests.get(abstractQuest);
            if (!progression.isAchieved() && abstractQuest.getType() == questType) {

                if (abstractQuest instanceof LocationQuest quest) {
                    // vérifier le monde
                    final double distance = player.getLocation().distance(quest.getRequiredLocation());

                    if (distance <= quest.getRadius()) {

                        progression.setAchieved();
                        QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());
                        RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());

                        if (!Synchronization.isSynchronised()) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
