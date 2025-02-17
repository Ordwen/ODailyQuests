package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;

public class Modes implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public Modes(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private int questsMode;
    private int timestampMode;

    @Override
    public void load() {
        questsMode = configurationFile.getConfigFile().getInt("quests_mode");
        timestampMode = configurationFile.getConfigFile().getInt("timestamp_mode");
    }

    private static Modes getInstance() {
        return ConfigFactory.getConfig(Modes.class);
    }

    public static int getQuestsMode() {
        return getInstance().questsMode;
    }

    public static int getTimestampMode() {
        return getInstance().timestampMode;
    }
}

