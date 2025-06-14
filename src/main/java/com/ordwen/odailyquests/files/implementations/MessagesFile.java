package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.APluginFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessagesFile extends APluginFile {

    private static MessagesFile instance;

    public MessagesFile(ODailyQuests plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    public void load() {
        file = new File(plugin.getDataFolder(), "messages.yml");

        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
            PluginLogger.info("Messages file created.");
        }

        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            PluginLogger.error("An error occurred while loading the messages file.");
            PluginLogger.error(e.getMessage());
        }
        PluginLogger.fine("Messages file successfully loaded.");
    }

    public String get(String path, String defaultValue) {
        return config.getString(path, defaultValue);
    }

    public static MessagesFile getInstance() {
        return instance;
    }
}