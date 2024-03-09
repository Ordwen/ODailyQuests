package com.ordwen.odailyquests.events.listeners.global;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.QuestType;
import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractGlobalChecker;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class BucketFillListener extends AbstractGlobalChecker implements Listener {

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;

        if (event.getItemStack().getType() == Material.MILK_BUCKET) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("BucketFillListener: onPlayerBucketFill summoned by " + event.getPlayer().getName());

            setPlayerQuestProgression(event.getPlayer(), 1, QuestType.MILKING);
        }
    }
}
