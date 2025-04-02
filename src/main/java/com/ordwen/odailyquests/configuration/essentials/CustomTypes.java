package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.quests.types.global.CustomQuest;

import java.util.HashSet;
import java.util.Set;

public class CustomTypes implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private final Set<String> types = new HashSet<>();

    public CustomTypes(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        types.clear();

        for (String customType : configurationFile.getConfig().getStringList("custom_types")) {
            types.add(customType);
            ODailyQuests.INSTANCE.registerQuestType(customType, CustomQuest.class);
        }
    }

    private static CustomTypes getInstance() {
        return ConfigFactory.getConfig(CustomTypes.class);
    }

    public static Set<String> getCustomTypes() {
        return getInstance().types;
    }
}
