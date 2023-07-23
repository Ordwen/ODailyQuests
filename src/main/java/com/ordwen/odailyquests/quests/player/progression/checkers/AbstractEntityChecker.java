package com.ordwen.odailyquests.quests.player.progression.checkers;

import com.ordwen.odailyquests.configuration.essentials.Synchronization;
import com.ordwen.odailyquests.configuration.functionalities.DisabledWorlds;
import com.ordwen.odailyquests.quests.player.progression.AbstractProgressionIncreaser;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.quests.types.EntityQuest;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Progression;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class AbstractEntityChecker extends AbstractProgressionIncreaser {

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
        final String worldName = player.getWorld().getName();

        if (DisabledWorlds.isWorldDisabled(worldName)) {
            return;
        }

        if (QuestsManager.getActiveQuests().containsKey(player.getName())) {

            final HashMap<AbstractQuest, Progression> playerQuests = QuestsManager.getActiveQuests().get(player.getName()).getPlayerQuests();

            for (AbstractQuest abstractQuest : playerQuests.keySet()) {

                final Progression progression = playerQuests.get(abstractQuest);
                if (!progression.isAchieved() && abstractQuest.getQuestType() == questType) {

                    boolean isRequiredEntity = false;

                    if (abstractQuest instanceof EntityQuest quest) {

                        System.out.println(quest.getEntityTypes() == null);
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
                            if (quest.getEntityName() != null) {
                                isRequiredEntity = (entityName.equals(quest.getEntityName()));
                            }
                        }

                    } else {
                        isRequiredEntity = true;
                    }

                    if (isRequiredEntity) {
                        increaseProgression(player, progression, abstractQuest, amount);
                        if (!Synchronization.isSynchronised()) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
