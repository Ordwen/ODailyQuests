package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class EntityBreedListener extends AbstractEntityChecker implements Listener {

    @EventHandler
    public void onEntityBreadEvent(EntityBreedEvent event) {
        if (event.isCancelled()) return;

        if (event.getBreeder() != null && event.getBreeder() instanceof Player player) {
            setPlayerQuestProgression(player, event.getEntityType(), null, 1, QuestType.BREED, null);
        }
    }
}
