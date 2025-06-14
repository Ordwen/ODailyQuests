package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.implementations.*;

public class FilesManager {

    private final ODailyQuests plugin;

    private final ConfigurationFile configurationFile;
    private final PlayerInterfaceFile playerInterfaceFile;
    private final TotalRewardsFile totalRewardsFile;
    private final ProgressionFile progressionFile;

    public FilesManager(ODailyQuests plugin) {
        this.plugin = plugin;

        this.configurationFile = new ConfigurationFile(plugin);
        this.playerInterfaceFile = new PlayerInterfaceFile(plugin);
        this.totalRewardsFile = new TotalRewardsFile(plugin);
        this.progressionFile = new ProgressionFile(plugin);
    }

    /**
     * Load all files.
     */
    public void load() {
        configurationFile.load();
        playerInterfaceFile.load();
        totalRewardsFile.load();
        progressionFile.load();

        new MessagesFile(plugin).load();
        new QuestsFiles(plugin).load();
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
}
