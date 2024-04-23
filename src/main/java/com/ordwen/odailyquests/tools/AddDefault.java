package com.ordwen.odailyquests.tools;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public class AddDefault {

    /**
     * Add a default parameter in a file if it is missing.
     *
     * @param path              path of the parameter to add
     * @param value             default value of the parameter to add
     * @param fileConfiguration file configuration where add the parameter
     * @param file              file to save the configuration
     */
    public static void addDefaultConfigItem(String path, Object value, FileConfiguration fileConfiguration, File file) {
        PluginLogger.warn("The parameter \"" + path + "\" was missing in one of your configuration files. It has been added automatically.");
        PluginLogger.warn("For more information about this addition, please visit the Wiki: https://ordwenplugins.gitbook.io/odailyquests/configuration/configuration-file#default-configuration-file");

        fileConfiguration.addDefault(path, value);
        fileConfiguration.options().copyDefaults(true);

        try {
            fileConfiguration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
