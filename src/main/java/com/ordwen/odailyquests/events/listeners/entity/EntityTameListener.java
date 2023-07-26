package com.ordwen.odailyquests.events.listeners.entity;

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
            setPlayerQuestProgression(player, event.getEntityType(), null, 1, QuestType.TAME, null);
        }
    }
}
