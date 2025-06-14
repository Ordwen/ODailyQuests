package com.ordwen.odailyquests.files;

import com.ordwen.odailyquests.ODailyQuests;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public abstract class APluginFile implements IPluginFile {

    protected final ODailyQuests plugin;

    protected FileConfiguration config;
    protected File file;

    protected APluginFile(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }
}