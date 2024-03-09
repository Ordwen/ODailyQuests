package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestType;
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
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("EntityBreedEvent: onEntityBreadEvent summoned by " + player.getName() + " for " + event.getEntityType() + ".");
            setPlayerQuestProgression(player, event.getEntityType(), null, 1, QuestType.BREED, null);
        }
    }
}
