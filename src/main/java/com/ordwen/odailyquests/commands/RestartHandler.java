package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import dev.norska.uar.api.UARRestartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class RestartHandler implements Listener {

    private final ODailyQuests plugin;

    public RestartHandler(ODailyQuests oDailyQuests) {
        this.plugin = oDailyQuests;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final String command = event.getMessage();
        if (command.equals("/stop") || command.equals("/restart")) {
            setServerStopping();
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        final String command = event.getCommand();
        if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("restart")) {
            setServerStopping();
        }
    }

    @EventHandler
    public void onUARRestart(UARRestartEvent event) {
        setServerStopping();
    }

    private void setServerStopping() {
        PluginLogger.warn("Server is stopping. The datas will be saved in synchronous mode.");
        PluginLogger.warn("If you think this is a mistake, please contact the developer!");
        plugin.setServerStopping(true);
    }
}
