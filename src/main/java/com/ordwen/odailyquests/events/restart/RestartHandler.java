package com.ordwen.odailyquests.events.restart;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;

public class RestartHandler {

    private final ODailyQuests plugin;

    public RestartHandler(ODailyQuests oDailyQuests) {
        this.plugin = oDailyQuests;
    }

    public void setServerStopping() {
        PluginLogger.warn("Server is stopping. The datas will be saved in synchronous mode.");
        PluginLogger.warn("If you think this is a mistake, please contact the developer!");
        plugin.setServerStopping(true);
    }

    public void registerSubClasses() {
        plugin.getServer().getPluginManager().registerEvents(new RestartCommandListener(plugin), plugin);

        if (plugin.getServer().getPluginManager().getPlugin("UltimateAutoRestart") != null) {
            plugin.getServer().getPluginManager().registerEvents(new UARListener(plugin), plugin);
        }
    }
}
