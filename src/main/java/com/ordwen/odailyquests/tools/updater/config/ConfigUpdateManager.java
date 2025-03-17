package com.ordwen.odailyquests.tools.updater.config;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
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

            if (compareVersions(configVersion, updateVersion) < 0) {
                PluginLogger.warn("Applying update: " + updateVersion);
                updater.apply(plugin, currentVersion);
            }
        }

        plugin.saveConfig();
    }

    private int compareVersions(String v1, String v2) {
        final String[] parts1 = v1.split("\\.");
        final String[] parts2 = v2.split("\\.");
        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            if (num1 != num2) {
                return Integer.compare(num1, num2);
            }
        }
        return 0;
    }
}
