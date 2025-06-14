package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.PluginUtils;

public class ItemsAdderEnabled implements IConfigurable {

    private static boolean loaded = false;

    private final ConfigurationFile configurationFile;

    public ItemsAdderEnabled(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;

    @Override
    public void load() {
        final String path = "use_itemsadder";
        isEnabled = configurationFile.getConfig().getBoolean(path);
        if (isEnabled() && !PluginUtils.isPluginEnabled("ItemsAdder")) {
            PluginLogger.warn("ItemsAdder is not installed on the server but the option is enabled in the config.");
            PluginLogger.warn("Disabling 'use_itemsadder' option, otherwise quests will not load.");
            isEnabled = false;
        }
        if (isEnabled) CustomFurnaceResults.setEnabled(true);
    }

    private static ItemsAdderEnabled getInstance() {
        return ConfigFactory.getConfig(ItemsAdderEnabled.class);
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
