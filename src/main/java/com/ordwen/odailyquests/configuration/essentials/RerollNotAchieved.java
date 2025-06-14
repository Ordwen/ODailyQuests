package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;

public class RerollNotAchieved implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private boolean rerollIfNotAchieved;

    public RerollNotAchieved(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final String path = "reroll_only_if_not_achieved";
        rerollIfNotAchieved = configurationFile.getConfig().getBoolean(path);
    }

    private static RerollNotAchieved getInstance() {
        return ConfigFactory.getConfig(RerollNotAchieved.class);
    }

    public static boolean isRerollIfNotAchieved() {
        return getInstance().rerollIfNotAchieved;
    }
}
