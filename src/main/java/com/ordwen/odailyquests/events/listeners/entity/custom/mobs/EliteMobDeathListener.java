package com.ordwen.odailyquests.events.listeners.entity.custom.mobs;

import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.ordwen.odailyquests.quests.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractEntityChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EliteMobDeathListener extends AbstractEntityChecker implements Listener {

    @EventHandler
    public void onEliteMobsDeathEvent(EliteMobDeathEvent event) {
        if (event.getEntityDeathEvent().getEntity().getKiller() != null) {
            setPlayerQuestProgression(event.getEntityDeathEvent().getEntity().getKiller(), null, event.getEliteEntity().getName().substring(event.getEliteEntity().getName().indexOf(' ')+1), 1, QuestType.CUSTOM_MOBS, null);
        }
    }
}
