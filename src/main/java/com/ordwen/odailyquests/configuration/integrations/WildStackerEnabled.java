package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class WildStackerEnabled implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public WildStackerEnabled(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private boolean isEnabled;

    @Override
    public void load() {
        final String path = "use_wildstacker";
        isEnabled = configurationFiles.getConfigFile().getBoolean(path);

        if (isEnabled && Bukkit.getPluginManager().getPlugin("WildStacker") == null) {
            PluginLogger.error("WildStacker is enabled in the config but the plugin is not installed.");
            PluginLogger.error("Disabling WildStacker integration, otherwise KILL quests will not work properly.");
            isEnabled = false;
        }
    }

    public static WildStackerEnabled getInstance() {
        return ConfigFactory.getConfig(WildStackerEnabled.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }
}
