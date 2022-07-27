package com.ordwen.odailyquests.events.listeners.entity;

import com.ordwen.odailyquests.configuration.integrations.WildStackerEnabled;
import com.ordwen.odailyquests.events.antiglitch.EntitySource;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener extends AbstractEntityChecker implements Listener {

    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        final boolean isEntityFromSpawner = EntitySource.isEntityFromSpawner(event.getEntity());
        EntitySource.removeEntityFromSpawner(event.getEntity());

        if (WildStackerEnabled.isEnabled()) return;
        if (event.getEntity().getKiller() == null) return;
        if (isEntityFromSpawner) return;

        setPlayerQuestProgression(event.getEntity().getKiller(), event.getEntityType(), null, 1, QuestType.KILL, null);
    }
}
