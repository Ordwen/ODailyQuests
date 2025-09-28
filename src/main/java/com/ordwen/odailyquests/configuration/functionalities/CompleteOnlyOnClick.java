package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;

public class CompleteOnlyOnClick implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private boolean enabled;

    public CompleteOnlyOnClick(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        enabled = configurationFile.getConfig().getBoolean("complete_only_on_click");
    }

    private static CompleteOnlyOnClick getInstance() {
        return ConfigFactory.getConfig(CompleteOnlyOnClick.class);
    }

    public static boolean isEnabled() {
        return getInstance().enabled;
    }
}
