package com.ordwen.odailyquests.events.listeners.integrations.nexo;

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.integrations.NexoEnabled;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NexoItemsLoadedListener implements Listener {

    private final ODailyQuests plugin;

    public NexoItemsLoadedListener(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNexoItemsLoaded(NexoItemsLoadedEvent event) {
        PluginLogger.info("Nexo updated its data. Reloading...");
        NexoEnabled.setLoaded(true);
        plugin.getReloadService().reload();
    }
}
