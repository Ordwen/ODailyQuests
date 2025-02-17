package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class RerollNotAchieved implements IConfigurable {

    private final ConfigurationFiles configurationFiles;
    private boolean rerollIfNotAchieved;

    public RerollNotAchieved(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    @Override
    public void load() {
        final String path = "reroll_only_if_not_achieved";
        rerollIfNotAchieved = configurationFiles.getConfigFile().getBoolean(path);
    }

    private static RerollNotAchieved getInstance() {
        return ConfigFactory.getConfig(RerollNotAchieved.class);
    }

    public static boolean isRerollIfNotAchieved() {
        return getInstance().rerollIfNotAchieved;
    }
}
