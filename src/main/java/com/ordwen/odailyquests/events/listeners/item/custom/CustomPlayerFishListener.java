package com.ordwen.odailyquests.events.listeners.item.custom;

import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.quests.player.progression.PlayerProgressor;
import net.Indyuce.mmocore.api.event.CustomPlayerFishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomPlayerFishListener extends PlayerProgressor implements Listener {

    @EventHandler
    public void onMMOFish(CustomPlayerFishEvent event) {
        if (event.isCancelled()) {
            Debugger.write("CustomPlayerFishListener: onMMOFish cancelled for " + event.getPlayer().getName());
            return;
        }

        Debugger.write("CustomPlayerFishListener: onMMOFish for " + event.getPlayer().getName());

        if (event.getCaught() != null) {
            Debugger.write("CustomPlayerFishListener: onMMOFish summoned by " + event.getPlayer().getName() + " for " + event.getCaught().getType());
            setPlayerQuestProgression(event, event.getPlayer(), event.getCaught().getAmount(), "FISH");
        }
    }
}
