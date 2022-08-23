package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.AddDefault;
import org.bukkit.configuration.file.FileConfiguration;

public class QuestsAmount {

    /*
    QuestsManager ( -> categorized)
    PlayerInterface
    + PlaceholderAPI
    + HolographicDisplays
    + AdminCommands ( -> complete)
    + PlayerQuests
     */

    private final ConfigurationFiles configurationFiles;

    public QuestsAmount(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static int questsAmount;

    public void loadQuestsAmount() {

        final FileConfiguration config = configurationFiles.getConfigFile();

        if (!config.contains("quests_amount")) AddDefault.addDefaultConfigItem("quests_item", 3, config, configurationFiles.getFile());
        questsAmount = config.getInt("quests_amount");
    }

    public static int getQuestsAmount() {
        return questsAmount;
    }
}
