package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.PluginUtils;

public class NexoEnabled implements IConfigurable {

    private static boolean loaded = false;

    private final ConfigurationFile configurationFile;

    public NexoEnabled(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;

    @Override
    public void load() {
        final String path = "use_nexo";
        isEnabled = configurationFile.getConfig().getBoolean(path);
        if (isEnabled && !PluginUtils.isPluginEnabled("Nexo")) {
            PluginLogger.warn("Nexo is not installed on the server but the option is enabled in the config.");
            PluginLogger.warn("Disabling 'use_nexo' option, otherwise quests will not load.");
            isEnabled = false;
        }
        if (isEnabled) CustomFurnaceResults.setEnabled(true);
    }

    private static NexoEnabled getInstance() {
        return ConfigFactory.getConfig(NexoEnabled.class);
    }

    public static void setLoaded(boolean isLoaded) {
        NexoEnabled.loaded = isLoaded;
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }

    public static boolean isLoaded() {
        return loaded && getInstance().isEnabled;
    }
}
