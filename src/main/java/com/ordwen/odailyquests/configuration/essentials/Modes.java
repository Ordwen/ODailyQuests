package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import lombok.Getter;

public class Modes {

    private final ConfigurationFiles configurationFiles;

    public Modes(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static String storageMode;

    public void loadPluginModes() {
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            questSystem.setQuestsMode(configurationFiles.getConfigFile().getInt(questSystem.getConfigPath() + "quests_mode"));
            questSystem.setTimeStampMode(configurationFiles.getConfigFile().getInt(questSystem.getConfigPath() + "timestamp_mode"));
            questSystem.setTemporalityMode(configurationFiles.getConfigFile().getInt(questSystem.getConfigPath() + "temporality_mode"));
        });
        storageMode = configurationFiles.getConfigFile().getString("storage_mode");
    }

    /**
     * Get quests mode.
     *
     * @return plugin mode.
     */
    public static int getQuestsMode(QuestSystem questSystem) {
        return questSystem.getTimeStampMode();
    }

    /**
     * Get timestamp mode.
     *
     * @return plugin mode.
     */
    public static int getTimestampMode(QuestSystem questSystem) {
        return questSystem.getTimeStampMode();
    }

    /**
     * Get storage mode.
     *
     * @return plugin mode.
     */
    public static String getStorageMode() {
        return storageMode;
    }

}

