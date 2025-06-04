package com.ordwen.odailyquests.tools.updater.database;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.updater.database.updates.Update0to1;
import com.ordwen.odailyquests.tools.updater.database.updates.Update1to2;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class DatabaseUpdateManager {

    private final ODailyQuests plugin;
    private final Map<String, IDatabaseUpdater> updaters = new LinkedHashMap<>();

    public DatabaseUpdateManager(ODailyQuests plugin) {
        this.plugin = plugin;

        updaters.put("1", new Update0to1(plugin));
        updaters.put("2", new Update1to2(plugin));
    }

    public void runUpdates() {
        final FileConfiguration configFile = plugin.getFilesManager().getConfigurationFile().getConfig();
        final String currentVersion = configFile.getString("database_version", "0");

        for (Map.Entry<String, IDatabaseUpdater> entry : updaters.entrySet()) {
            final String updateVersion = entry.getKey();
            final IDatabaseUpdater updater = entry.getValue();

            if (Integer.parseInt(currentVersion) < Integer.parseInt(updateVersion)) {
                PluginLogger.warn("Applying database update: " + updateVersion);
                updater.apply(plugin, updateVersion);
            }
        }
    }
}