package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;

public class FilesManager {

    private final ConfigurationFile configurationFile;
    private final PlayerInterfaceFile playerInterfaceFile;
    private final TotalRewardsFile totalRewardsFile;
    private final ProgressionFile progressionFile;
    private final MessagesFile messagesFile;
    private final QuestsFiles questsFiles;

    public FilesManager(ODailyQuests plugin) {
        this.configurationFile = new ConfigurationFile(plugin);
        this.playerInterfaceFile = new PlayerInterfaceFile(plugin);
        this.totalRewardsFile = new TotalRewardsFile(plugin);
        this.progressionFile = new ProgressionFile(plugin);
        this.messagesFile = new MessagesFile(plugin);
        this.questsFiles = new QuestsFiles(plugin);
    }

    /**
     * Load all files.
     */
    public void load() {
        configurationFile.load();
        playerInterfaceFile.load();
        totalRewardsFile.load();
        progressionFile.load();
        messagesFile.load();
        questsFiles.load();
    }

    public ConfigurationFile getConfigurationFile() {
        return configurationFile;
    }

    public PlayerInterfaceFile getPlayerInterfaceFile() {
        return playerInterfaceFile;
    }

    public TotalRewardsFile getTotalRewardsFile() {
        return totalRewardsFile;
    }

    public ProgressionFile getProgressionFile() {
        return progressionFile;
    }

    public MessagesFile getMessagesFile() {
        return messagesFile;
    }

    public QuestsFiles getQuestsFiles() {
        return questsFiles;
    }
}
