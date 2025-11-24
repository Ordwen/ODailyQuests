package com.ordwen.odailyquests.files.base;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Generic base for any YAML-backed plugin file.
 * This is reused by the main plugin and addons.
 */
public abstract class APluginFile<P extends JavaPlugin> implements IPluginFile {

    protected final P plugin;
    protected final String fileName;

    protected FileConfiguration config;
    protected File file;

    protected APluginFile(P plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    @Override
    public final void load() {
        file = new File(plugin.getDataFolder(), fileName);

        boolean fileJustCreated = false;
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
            fileJustCreated = true;
        }

        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            onLoadError(e, fileJustCreated);
            return;
        }

        onPostLoad(fileJustCreated);
    }

    /**
     * Called if an error occurs while loading the YAML file.
     */
    protected void onLoadError(Exception e, boolean fileJustCreated) {
        // default: no-op, subclasses log if needed
    }

    /**
     * Called after the YAML has been successfully loaded.
     *
     * @param fileJustCreated true if the file has just been created from resources
     */
    protected void onPostLoad(boolean fileJustCreated) {
        // default: no-op, subclasses log if needed
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public File getFile() {
        return file;
    }
}
