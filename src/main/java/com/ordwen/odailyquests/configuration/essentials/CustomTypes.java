package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.types.global.CustomQuest;

public class CustomTypes {

    private final ConfigurationFiles configurationFiles;

    public CustomTypes(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }


    public void loadCustomTypes() {
        for (String customType : configurationFiles.getConfigFile().getStringList("custom_types")) {
            ODailyQuests.INSTANCE.registerQuestType(customType, CustomQuest.class);
        }
    }
}
