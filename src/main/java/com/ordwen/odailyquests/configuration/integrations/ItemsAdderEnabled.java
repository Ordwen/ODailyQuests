package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.configuration.essentials.CustomFurnaceResults;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class ItemsAdderEnabled implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public ItemsAdderEnabled(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private boolean isEnabled;
    private boolean isLoaded;

    @Override
    public void load() {
        final String path = "use_itemsadder";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);
        if (isEnabled() && !Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
            PluginLogger.warn("ItemsAdder is not installed on the server but the option is enabled in the config.");
            PluginLogger.warn("Disabling 'use_itemsadder' option, otherwise quests will not load.");
            isEnabled = false;
        }
        if (isEnabled) CustomFurnaceResults.setEnabled(true);
    }

    public static ItemsAdderEnabled getInstance() {
        return ConfigFactory.getConfig(ItemsAdderEnabled.class);
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
