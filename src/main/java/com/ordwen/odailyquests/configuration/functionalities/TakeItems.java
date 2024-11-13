package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class TakeItems {

    private static boolean enabled = false;
    private final ConfigurationFiles configurationFiles;

    public TakeItems(final ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    public static boolean isTakeItemsEnabled() {
        return enabled;
    }

    public void loadTakeItems() {
        final String path = "take_items_for_get_quests";
        enabled = configurationFiles.getConfigFile().getBoolean(path);
    }
}
