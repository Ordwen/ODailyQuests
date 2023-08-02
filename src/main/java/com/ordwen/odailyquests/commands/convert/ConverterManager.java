package com.ordwen.odailyquests.commands.convert;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;

public class ConverterManager {

    final ODailyQuests oDailyQuests;
    public ConverterManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    public boolean convert(String oldFormat, String newFormat) {

        switch (oldFormat) {
            case "YAML", "yaml" -> {
                switch (newFormat) {
                    case "MySQL", "mysql" -> {
                        return new YAMLtoMySQLConverter().convert(oDailyQuests);
                    }
                    case "H2", "h2" -> {
                        return new YAMLtoH2Converter().convert(oDailyQuests);
                    }
                    default -> {
                        PluginLogger.error("The new format is not supported.");
                        return false;
                    }
                }
            }
            default -> {
                PluginLogger.error("The old format is not supported.");
                return false;
            }
        }
    }
}
