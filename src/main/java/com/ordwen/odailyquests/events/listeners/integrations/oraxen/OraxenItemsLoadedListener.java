package com.ordwen.odailyquests.events.listeners.integrations.oraxen;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.OraxenEnabled;
import com.ordwen.odailyquests.tools.PluginLogger;
import io.th0rgal.oraxen.api.events.OraxenItemsLoadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OraxenItemsLoadedListener implements Listener {

    private final ODailyQuests plugin;

    public OraxenItemsLoadedListener(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onOraxenItemsLoaded(OraxenItemsLoadedEvent event) {
        PluginLogger.info("Oraxen updated its data. Reloading...");
        OraxenEnabled.setLoaded(true);
        plugin.getReloadService().reload();
    }
}
