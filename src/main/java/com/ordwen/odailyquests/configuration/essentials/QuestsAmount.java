package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.configuration.file.FileConfiguration;

public class QuestsAmount implements IConfigurable {

    private final ConfigurationFiles configurationFiles;

    public QuestsAmount(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private int totalQuestsAmount;
    private int easyQuestsAmount;
    private int mediumQuestsAmount;
    private int hardQuestsAmount;

    @Override
    public void load() {
        System.out.println("LOAD LOAD LOAD");
        final FileConfiguration config = configurationFiles.getConfigFile();

        if (Modes.getQuestsMode() == 1) {
            totalQuestsAmount = config.getInt("global_quests_amount");
        } else if (Modes.getQuestsMode() == 2) {
            easyQuestsAmount = config.getInt("easy_quests_amount");
            mediumQuestsAmount = config.getInt("medium_quests_amount");
            hardQuestsAmount = config.getInt("hard_quests_amount");

            totalQuestsAmount = easyQuestsAmount + mediumQuestsAmount + hardQuestsAmount;
        }
    }

    public static int getQuestsAmountByCategory(String name) {
        return switch (name) {
            case "easyQuests" -> getInstance().easyQuestsAmount;
            case "mediumQuests" -> getInstance().mediumQuestsAmount;
            case "hardQuests" -> getInstance().hardQuestsAmount;
            default -> -1;
        };
    }

    private static QuestsAmount getInstance() {
        return ConfigFactory.getConfig(QuestsAmount.class);
    }

    public static int getQuestsAmount() {
        return getInstance().totalQuestsAmount;
    }

    public static int getEasyQuestsAmount() {
        return getInstance().easyQuestsAmount;
    }

    public static int getMediumQuestsAmount() {
        return getInstance().mediumQuestsAmount;
    }

    public static int getHardQuestsAmount() {
        return getInstance().hardQuestsAmount;
    }
}
