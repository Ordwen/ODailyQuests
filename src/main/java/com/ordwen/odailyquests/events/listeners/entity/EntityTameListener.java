package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;

public class EntityTameListener extends AbstractEntityChecker implements Listener {

    @EventHandler
    public void onEntityTameEvent(EntityTameEvent event) {
        if (event.isCancelled()) return;

        if (event.getOwner() instanceof Player player) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("EntityTameEvent: onEntityTameEvent summoned by " + player.getName() + " for " + event.getEntityType() + ".");

            setPlayerQuestProgression(player, event.getEntityType(), null, 1, QuestType.TAME, null);
        }
    }
}
