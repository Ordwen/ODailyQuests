package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractGlobalChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerRespawnListener extends AbstractGlobalChecker implements Listener {

    @EventHandler
    public void onPlayerRespawn(org.bukkit.event.player.PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        if (player.hasMetadata("odailyquests:dead")) {
            player.removeMetadata("odailyquests:dead", ODailyQuests.INSTANCE);

            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("PlayerRespawnListener: onPlayerRespawn summoned by " + player.getName());
            setPlayerQuestProgression(player, 1, QuestType.PLAYER_DEATH);
        }
    }
}
