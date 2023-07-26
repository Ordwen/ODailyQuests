package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractGlobalChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends AbstractGlobalChecker implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        setPlayerQuestProgression(event.getEntity(), 1, QuestType.PLAYER_DEATH);
    }
}
