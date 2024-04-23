package com.ordwen.odailyquests.events.listeners.item;

import com.ordwen.odailyquests.configuration.essentials.Debugger;

import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class PlayerFishListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onPlayerFishEvent(PlayerFishEvent event) {
        System.out.println("PlayerFishListener: onPlayerFishEvent");
        if (event.isCancelled()) return;

        System.out.println("=============================================================");
        System.out.println("not cancelled");
        System.out.println("state: " + event.getState());
        System.out.println("instanceof: " + (event.getCaught() instanceof Item));
        if (event.getCaught() != null) System.out.println("type: " + event.getCaught().getType());
        System.out.println("=============================================================");

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item item) {
            Debugger.addDebug("=========================================================================================");
            Debugger.addDebug("PlayerFishListener: onPlayerFishEvent summoned by " + event.getPlayer().getName() + " for " + item.getItemStack().getType() + ".");

            setPlayerQuestProgression(event, event.getPlayer(), 1, "FISH");
        }
    }
}
