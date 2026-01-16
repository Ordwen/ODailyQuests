package com.ordwen.odailyquests.events.listeners.integrations.emf;

import com.oheers.fish.api.events.EMFFishCaughtEvent;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EMFFishCaughtListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onEMFFishCaughtEvent(EMFFishCaughtEvent event) {
        Debugger.write("[EMFFishCaughtListener] Caught a fish via EvenMoreFish.");

        if (event.isCancelled()) {
            Debugger.write("[EMFFishCaughtListener] Event is cancelled, cancelling progression.");
            return;
        }

        setPlayerQuestProgression(event, event.getPlayer(), 1, "EMF_FISH");
    }
}