package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.PluginUtils;

public class OraxenEnabled implements IConfigurable {

    private static boolean loaded = false;

    private final ConfigurationFile configurationFile;

    public OraxenEnabled(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;

    @Override
    public void load() {
        final String path = "use_oraxen";
        isEnabled = configurationFile.getConfig().getBoolean(path);
        if (isEnabled && !PluginUtils.isPluginEnabled("Oraxen")) {
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
        loaded = isLoaded;
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }

    public static boolean isLoaded() {
        return loaded && getInstance().isEnabled;
    }
}
