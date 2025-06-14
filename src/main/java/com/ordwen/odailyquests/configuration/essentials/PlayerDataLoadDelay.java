package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;

public class PlayerDataLoadDelay implements IConfigurable {

    private final ConfigurationFile configurationFile;

    private long delay;

    public PlayerDataLoadDelay(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        double value = configurationFile.getConfig().getDouble("player_data_load_delay", 0.5);
        delay = Math.round(value * 1000);
    }

    private static PlayerDataLoadDelay getInstance() {
        return ConfigFactory.getConfig(PlayerDataLoadDelay.class);
    }

    public static long getDelay() {
        return getInstance().delay;
    }
}
