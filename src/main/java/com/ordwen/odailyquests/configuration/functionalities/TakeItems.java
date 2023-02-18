package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class TakeItems {

    private final ConfigurationFiles configurationFiles;

    public TakeItems(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static boolean enabled = false;

    public void loadTakeItems() {
        final String path = "take_items_for_get_quests";
        enabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static boolean isTakeItemsEnabled() {
        return enabled;
    }
}
