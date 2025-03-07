package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.quests.types.global.CustomQuest;

public class CustomTypes implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public CustomTypes(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        for (String customType : configurationFile.getConfig().getStringList("custom_types")) {
            ODailyQuests.INSTANCE.registerQuestType(customType, CustomQuest.class);
        }
    }
}
