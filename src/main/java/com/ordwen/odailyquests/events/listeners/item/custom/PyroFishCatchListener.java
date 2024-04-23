package com.ordwen.odailyquests.events.listeners.item.custom;

import me.arsmagica.API.PyroFishCatchEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PyroFishCatchListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPyroFishCatch(PyroFishCatchEvent event) {
        System.out.println("PyroFishCatchListener: onPyroFishCatch");
        System.out.println("player: " + event.a().getName());
        System.out.println("tier: " + event.getTier());
    }
}
