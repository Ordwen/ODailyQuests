package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Debugger;

public class FilesManager {

    private final ODailyQuests oDailyQuests;

    public FilesManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Load all files.
     */
    public void loadAllFiles() {

        oDailyQuests.getConfigurationFiles().loadConfigurationFiles();
        oDailyQuests.getConfigurationFiles().loadMessagesFiles();

        new QuestsFiles(oDailyQuests).loadQuestsFiles();
        new ProgressionFile(oDailyQuests).loadProgressionFile();
        new HologramsFile(oDailyQuests).loadHologramsFile();
        new PlayerInterfaceFile(oDailyQuests).loadPlayerInterfaceFile();
    }
}
