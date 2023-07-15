package com.ordwen.odailyquests.events.listeners.integrations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.tools.PluginLogger;
import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ItemsAdderLoadDataListener implements Listener {

    private final ODailyQuests plugin;

    public ItemsAdderLoadDataListener(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemsAdderLoadData(ItemsAdderLoadDataEvent event) {
        PluginLogger.info("ItemsAdder updated its data. Reloading...");
        ItemsAdderEnabled.setLoaded(true);
        plugin.getReloadService().reload();
    }
}
