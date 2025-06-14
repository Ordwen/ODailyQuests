package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;

public class Prefix implements IConfigurable {

    private final ConfigurationFile configurationFile;

    private String str;

    public Prefix(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        str = configurationFile.getConfig().getString("prefix", "");
    }

    public String getPrefixInternal() {
        return str;
    }

    private static Prefix getInstance() {
        return ConfigFactory.getConfig(Prefix.class);
    }

    public static String getPrefix() {
        return getInstance().getPrefixInternal();
    }
}
