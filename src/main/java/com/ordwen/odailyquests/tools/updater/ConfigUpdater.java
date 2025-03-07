package com.ordwen.odailyquests.tools.updater;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.FilesManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;

public abstract class ConfigUpdater implements IConfigUpdater {

    protected final File configFile;
    protected final FileConfiguration config;

    protected final File playerInterfaceFile;
    protected final FileConfiguration playerInterface;

    protected ConfigUpdater(ODailyQuests plugin) {
        final FilesManager filesManager = plugin.getFilesManager();

        this.configFile = filesManager.getConfigurationFile().getFile();
        this.config = filesManager.getConfigurationFile().getConfig();

        this.playerInterfaceFile = filesManager.getPlayerInterfaceFile().getFile();
        this.playerInterface = filesManager.getPlayerInterfaceFile().getConfig();
    }

    /**
     * Add a default parameter in a file if it is missing.
     *
     * @param path              path of the parameter to add
     * @param value             default value of the parameter to add
     * @param fileConfiguration file configuration where add the parameter
     * @param file              file to save the configuration
     */
    protected void setDefaultConfigItem(String path, Object value, FileConfiguration fileConfiguration, File file) {
        if (fileConfiguration.contains(path)) {
            return;
        }

        fileConfiguration.set(path, value);

        try {
            fileConfiguration.save(file);
            PluginLogger.warn("Parameter \"" + path + "\" was missing in one of your configuration files. It has been added automatically.");
        } catch (IOException e) {
            PluginLogger.error("Error while saving the configuration file.");
        }
    }

    protected void updateVersion(String version) {
        config.set("version", version);
        try {
            config.save(configFile);
        } catch (IOException e) {
            PluginLogger.error("Error while saving the configuration file.");
        }
    }
}
