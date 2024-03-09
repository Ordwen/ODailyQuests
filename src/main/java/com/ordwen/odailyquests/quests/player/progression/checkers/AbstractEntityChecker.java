package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.EntityQuest;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import com.ordwen.odailyquests.quests.player.progression.QuestProgressUtils;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class AbstractEntityChecker {

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

        Debugger.addDebug("EntityChecker: setPlayerQuestProgression summoned by " + player.getName() + " for " + entityType + " with amount " + amount + " and quest type " + questType + ".");

        if (DisabledWorlds.isWorldDisabled(player.getWorld().getName())) {
            return;
        }

        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {

            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();

            Debugger.addDebug("EntityChecker: player " + player.getName() + "currently have " + playerQuests.size() + " quests in progress");

            for (AbstractQuest abstractQuest : playerQuests.keySet()) {

                final Progression progression = playerQuests.get(abstractQuest);
                if (!progression.isAchieved() && abstractQuest.getQuestType() == questType) {

                    Debugger.addDebug("EntityChecker: player " + player.getName() + " is currently progressing on " + abstractQuest.getQuestType() + " quest " + abstractQuest.getQuestName());

                    boolean isRequiredEntity = false;

                    if (abstractQuest instanceof EntityQuest quest) {

                        Debugger.addDebug("EntityChecker: quest " + abstractQuest.getQuestName() + " is an EntityQuest");

                        if (quest.getEntityTypes() == null) isRequiredEntity = true;
                        else {
                            for (EntityType type : quest.getEntityTypes()) {
                                isRequiredEntity = (type == entityType);
                                if (isRequiredEntity) break;
                            }
                        }

                        if (isRequiredEntity) {
                            if (quest.getDyeColor() != null) {
                                isRequiredEntity = (dyeColor == quest.getDyeColor());
                            }
                            if (quest.getEntityNames() != null && !quest.getEntityNames().isEmpty()) {
                                isRequiredEntity = (quest.getEntityNames().contains(entityName));
                            }
                        }

                    } else {
                        isRequiredEntity = true;
                    }

                    if (isRequiredEntity) {

                        Debugger.addDebug("EntityChecker: player " + player.getName() + " is progressing on " + abstractQuest.getQuestName() + " with amount " + amount);

                        QuestProgressUtils.actionQuest(player, progression, abstractQuest, amount);
                        if (!Synchronization.isSynchronised()) break;
                    }
                }
            }
        }
    }
}
