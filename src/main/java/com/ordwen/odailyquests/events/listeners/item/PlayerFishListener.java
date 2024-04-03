package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.checkers.AbstractItemChecker;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishListener extends AbstractItemChecker implements Listener {

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.isCancelled()) return;

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item item) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("PlayerFishListener: onPlayerFishEvent summoned by " + event.getPlayer().getName() + " for " + item.getItemStack().getType() + ".");

            setPlayerQuestProgression(event.getPlayer(), item.getItemStack(), 1, "FISH");
        }
    }
}
