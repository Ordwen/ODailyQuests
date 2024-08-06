package com.ordwen.odailyquests.quests.player.progression.listeners;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.functionalities.rewards.GlobalReward;
import com.ordwen.odailyquests.api.events.AllQuestsCompletedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AllQuestsCompletedListener implements Listener {

    @EventHandler
    public void onAllQuestsCompletedEvent(AllQuestsCompletedEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(player).runDelayed(() -> GlobalReward.sendGlobalReward(player.getName()), null, 1L);
    }
}
