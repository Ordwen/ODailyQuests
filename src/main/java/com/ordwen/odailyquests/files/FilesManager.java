package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;

public class FilesManager {

    private final ODailyQuests oDailyQuests;

    private final ConfigurationFile configurationFile;
    private final PlayerInterfaceFile playerInterfaceFile;

    public FilesManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;

        this.configurationFile = new ConfigurationFile(oDailyQuests);
        this.playerInterfaceFile = new PlayerInterfaceFile(oDailyQuests);
    }

    /**
     * Load all files.
     */
    public void load() {
        configurationFile.loadConfigurationFile();
        configurationFile.loadMessagesFiles();

        playerInterfaceFile.loadPlayerInterfaceFile();

        new QuestsFiles(oDailyQuests).loadQuestsFiles();
        new ProgressionFile(oDailyQuests).loadProgressionFile();
    }

    public ConfigurationFile getConfigurationFile() {
        return configurationFile;
    }

    public PlayerInterfaceFile getPlayerInterfaceFile() {
        return playerInterfaceFile;
    }
}
