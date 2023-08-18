package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandPreprocessListener implements Listener {

    private final ODailyQuests plugin;

    public CommandPreprocessListener(ODailyQuests oDailyQuests) {
        this.plugin = oDailyQuests;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        final String command = event.getMessage();
        if (command.equals("/stop") || command.equals("/restart")) {
            PluginLogger.warn("Server is stopping. The datas will be saved in synchronous mode.");
            PluginLogger.warn("If you think this is a mistake, please contact the developer!");
            plugin.setServerStopping(true);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        final String command = event.getCommand();
        if (command.equalsIgnoreCase("stop") || command.equalsIgnoreCase("restart")) {
            PluginLogger.warn("Server is stopping. The datas will be saved in synchronous mode.");
            PluginLogger.warn("If you think this is a mistake, please contact the developer!");
            plugin.setServerStopping(true);
        }
    }
}
