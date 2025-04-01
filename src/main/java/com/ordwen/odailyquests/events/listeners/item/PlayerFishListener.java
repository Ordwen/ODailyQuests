package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishListener extends PlayerProgressor implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFishEvent(PlayerFishEvent event) {
        if (event.isCancelled()) {
            Debugger.write("PlayerFishListener: onPlayerFishEvent cancelled for " + event.getPlayer().getName() + ".");
            return;
        }

        Debugger.write("PlayerFishListener: current state: " + event.getState());

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item item) {
            Debugger.write("PlayerFishListener: onPlayerFishEvent summoned by " + event.getPlayer().getName() + " for " + item.getItemStack().getType() + ".");
            setPlayerQuestProgression(event, event.getPlayer(), 1, "FISH");
        }
    }
}
