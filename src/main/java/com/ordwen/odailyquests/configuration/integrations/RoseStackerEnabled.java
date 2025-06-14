package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class RoseStackerEnabled implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public RoseStackerEnabled(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;

    @Override
    public void load() {
        final String path = "use_rosestacker";
        isEnabled = configurationFile.getConfig().getBoolean(path);

        if (isEnabled && Bukkit.getPluginManager().getPlugin("RoseStacker") == null) {
            PluginLogger.error("RoseStacker is enabled in the config but the plugin is not installed.");
            PluginLogger.error("Disabling RoseStacker integration, otherwise KILL quests will not work properly.");
            isEnabled = false;
        }
    }

    private static RoseStackerEnabled getInstance() {
        return ConfigFactory.getConfig(RoseStackerEnabled.class);
    }

    public static boolean isEnabled() {
        return getInstance().isEnabled;
    }
}
