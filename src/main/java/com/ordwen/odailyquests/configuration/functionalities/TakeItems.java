package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.AddDefault;

public class TakeItems {

    private final ConfigurationFiles configurationFiles;

    public TakeItems(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static boolean enabled = false;

    public void loadTakeItems() {
        final String path = "take_items_for_get_quests";
        if (configurationFiles.getConfigFile().contains(path)) {
            enabled = configurationFiles.getConfigFile().getBoolean(path);
        } else AddDefault.addDefaultConfigItem(path, false, configurationFiles.getConfigFile(), configurationFiles.getFile());
    }

    public static boolean isTakeItemsEnabled() {
        return enabled;
    }
}
