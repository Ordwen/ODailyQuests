package com.ordwen.odailyquests.configuration.functionalities;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;

public class TakeItem implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public TakeItem(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean enabled = false;

    @Override
    public void load() {
        final String path = "take_items_for_get_quests";
        enabled = configurationFile.getConfig().getBoolean(path);
    }

    private static TakeItem getInstance() {
        return ConfigFactory.getConfig(TakeItem.class);
    }

    public static boolean isTakeItemsEnabled() {
        return getInstance().enabled;
    }
}
