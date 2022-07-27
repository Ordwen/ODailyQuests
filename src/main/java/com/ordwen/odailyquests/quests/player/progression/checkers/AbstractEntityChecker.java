package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.quests.player.progression.types.AbstractQuest;
import com.ordwen.odailyquests.quests.player.progression.types.EntityQuest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.rewards.RewardManager;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class AbstractEntityChecker {

    /**
     * Increase player quest progression.
     *
     * @param player     the player to increase progression for.
     * @param amount     the amount to increase progression by.
     * @param questType  the quest type to increase progression for.
     * @param entityType the entity type to increase progression for.
     * @param dyeColor   the dye color of the sheep, if the entity is a sheep.
     */
    public void setPlayerQuestProgression(Player player, EntityType entityType, String entityName, int amount, QuestType questType, DyeColor dyeColor) {
        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            return;
        }

        final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();

        for (AbstractQuest abstractQuest : playerQuests.keySet()) {

            final Progression progression = playerQuests.get(abstractQuest);
            if (!progression.isAchieved() && abstractQuest.getType() == questType) {

                if (abstractQuest instanceof EntityQuest quest) {
                    boolean isRequiredEntity;

                    if (quest.getEntityType() == null) isRequiredEntity = true;
                    else isRequiredEntity = (quest.getEntityType() == entityType);

                    if (isRequiredEntity) {
                        if (quest.getDyeColor() != null) isRequiredEntity = (dyeColor == quest.getDyeColor());
                        if (quest.getEntityName() != null)
                            isRequiredEntity = (entityName.equals(quest.getEntityName()));
                    }

                    if (isRequiredEntity) {
                        for (int i = 0; i < amount; i++) {
                            progression.increaseProgression();
                        }
                        if (progression.getProgression() >= quest.getAmountRequired()) {
                            progression.setAchieved();
                            QuestsManager.getActiveQuests().get(player.getName()).increaseAchievedQuests(player.getName());
                            RewardManager.sendAllRewardItems(quest.getQuestName(), player, quest.getReward());
                        }

                        if (!Synchronization.isSynchronised()) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
