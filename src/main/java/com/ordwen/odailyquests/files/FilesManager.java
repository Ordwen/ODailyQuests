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
        new QuestsFiles(oDailyQuests).loadQuestsFiles();
        new ProgressionFile(oDailyQuests).loadProgressionFile();
        new HologramsFile(oDailyQuests).loadHologramsFile();
        new PlayerInterfaceFile(oDailyQuests).loadPlayerInterfaceFile();
    }
}
