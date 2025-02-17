package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class OraxenEnabled implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public OraxenEnabled(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;
    private boolean isLoaded;

    @Override
    public void load() {
        final String path = "use_oraxen";
        isEnabled = configurationFile.getConfigFile().getBoolean(path);
        if (isEnabled && !Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            PluginLogger.warn("Oraxen is not installed on the server but the option is enabled in the config.");
            PluginLogger.warn("Disabling 'use_oraxen' option, otherwise quests will not load.");
            isEnabled = false;
        }
        if (isEnabled) CustomFurnaceResults.setEnabled(true);
    }

    private static OraxenEnabled getInstance() {
        return ConfigFactory.getConfig(OraxenEnabled.class);
    }

    public static void setLoaded(boolean isLoaded) {
        getInstance().isLoaded = isLoaded;
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }

    public static boolean isLoaded() {
        return getInstance().isLoaded && getInstance().isEnabled;
    }
}
