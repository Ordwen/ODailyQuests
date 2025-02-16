package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;

public class TakeItem implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public TakeItem(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private boolean enabled = false;

    @Override
    public void load() {
        final String path = "take_items_for_get_quests";
        enabled = configurationFiles.getConfigFile().getBoolean(path);
    }

    public static TakeItem getInstance() {
        return ConfigFactory.getConfig(TakeItem.class);
    }

    public static boolean isTakeItemsEnabled() {
        return getInstance().enabled;
    }
}
