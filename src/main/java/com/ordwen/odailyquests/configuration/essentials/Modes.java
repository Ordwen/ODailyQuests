package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Modes implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public Modes(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private int questsMode;
    private int timestampMode;
    private String storageMode;

    @Override
    public void load() {
        questsMode = configurationFiles.getConfigFile().getInt("quests_mode");
        timestampMode = configurationFiles.getConfigFile().getInt("timestamp_mode");
        storageMode = configurationFiles.getConfigFile().getString("storage_mode");
    }

    public static Modes getInstance() {
        return ConfigFactory.getConfig(Modes.class);
    }

    public static int getQuestsMode() {
        return getInstance().questsMode;
    }

    public static int getTimestampMode() {
        return getInstance().timestampMode;
    }

    public static String getStorageMode() {
        return getInstance().storageMode;
    }
}

