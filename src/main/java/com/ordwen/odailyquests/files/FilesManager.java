package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;

public class FilesManager {

    private final ODailyQuests oDailyQuests;

    public FilesManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Load all files.
     */
    public void loadAllFiles() {
        oDailyQuests.getConfigurationFile().loadConfigurationFile();
        oDailyQuests.getConfigurationFile().loadMessagesFiles();

        new QuestsFiles(oDailyQuests).loadQuestsFiles();
        new ProgressionFile(oDailyQuests).loadProgressionFile();
        new PlayerInterfaceFile(oDailyQuests).loadPlayerInterfaceFile();
    }
}
