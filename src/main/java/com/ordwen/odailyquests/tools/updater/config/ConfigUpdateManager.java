package com.ordwen.odailyquests.tools.updater.config;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.UpdateChecker;
import com.ordwen.odailyquests.tools.updater.config.updates.Update223to224;
import com.ordwen.odailyquests.tools.updater.config.updates.Update225to230;
import com.ordwen.odailyquests.tools.updater.config.updates.Update230to300;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConfigUpdateManager {

    private final ODailyQuests plugin;
    private final Map<String, IConfigUpdater> updaters = new LinkedHashMap<>();

    public ConfigUpdateManager(ODailyQuests plugin) {
        this.plugin = plugin;

        updaters.put("2.2.3", new Update223to224(plugin));
        updaters.put("2.2.5", new Update225to230(plugin));
        updaters.put("3.0.0", new Update230to300(plugin));
    }

    public void runUpdates() {
        final FileConfiguration configFile = plugin.getFilesManager().getConfigurationFile().getConfig();

        final String buildVersion = plugin.getDescription().getVersion();
        final int separatorIndex = buildVersion.indexOf('-');
        final String currentVersion = separatorIndex == -1 ? buildVersion : buildVersion.substring(0, separatorIndex);

        final String configVersion = configFile.getString("version", "0.0.0");

        for (Map.Entry<String, IConfigUpdater> entry : updaters.entrySet()) {
            final String updateVersion = entry.getKey();
            final IConfigUpdater updater = entry.getValue();

            if (UpdateChecker.compareVersions(configVersion, updateVersion) < 0) {
                PluginLogger.warn("Applying update: " + updateVersion);
                updater.apply(plugin, currentVersion);
            }
        }

        plugin.saveConfig();
    }
}
