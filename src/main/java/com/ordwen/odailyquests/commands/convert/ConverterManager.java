package com.ordwen.odailyquests.commands.convert;

public class ConverterManager {

    public boolean convert(String oldFormat, String newFormat) {

        switch (oldFormat) {
            case "YAML", "yaml":
                switch (newFormat) {
                    case "MySQL", "mysql":
                        //return new YAMLtoJSONConverter().convert();
                    case "H2", "h2":
                        //return new YAMLtoSQLConverter().convert();
                    default:
                        return false;
                }
        }
        return false;
    }
}
