package com.ordwen.odailyquests.events.restart;

import com.ordwen.odailyquests.ODailyQuests;
import dev.norska.uar.api.UARRestartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class UARListener extends RestartHandler implements Listener {

    public UARListener(ODailyQuests oDailyQuests) {
        super(oDailyQuests);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onUARRestart(UARRestartEvent event) {
        setServerStopping();
    }
}
