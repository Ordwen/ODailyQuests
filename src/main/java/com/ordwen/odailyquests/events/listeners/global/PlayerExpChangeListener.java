package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractGlobalChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerExpChangeListener extends AbstractGlobalChecker implements Listener {

    @EventHandler
    public void onPlayerExpChangeEvent(PlayerExpChangeEvent event) {
        setPlayerQuestProgression(event.getPlayer(), event.getAmount(), QuestType.EXP_POINTS);
    }
}
