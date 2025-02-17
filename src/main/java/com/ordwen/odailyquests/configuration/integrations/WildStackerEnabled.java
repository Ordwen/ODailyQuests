package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class WildStackerEnabled implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public WildStackerEnabled(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;

    @Override
    public void load() {
        final String path = "use_wildstacker";
        isEnabled = configurationFile.getConfigFile().getBoolean(path);

        if (isEnabled && Bukkit.getPluginManager().getPlugin("WildStacker") == null) {
            PluginLogger.error("WildStacker is enabled in the config but the plugin is not installed.");
            PluginLogger.error("Disabling WildStacker integration, otherwise KILL quests will not work properly.");
            isEnabled = false;
        }
    }

    private static WildStackerEnabled getInstance() {
        return ConfigFactory.getConfig(WildStackerEnabled.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }
}
