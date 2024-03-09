package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractGlobalChecker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class PlayerLevelChangeListener extends AbstractGlobalChecker implements Listener {

    @EventHandler
    public void onPlayerLevelChangeEvent(PlayerLevelChangeEvent event) {
        final int diff = event.getNewLevel() - event.getOldLevel();
        if (diff > 0) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("PlayerLevelChangeListener: onPlayerLevelChangeEvent summoned by " + event.getPlayer().getName());

            setPlayerQuestProgression(event.getPlayer(), diff, QuestType.EXP_LEVELS);
        }
    }
}
