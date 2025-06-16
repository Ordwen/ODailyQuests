package com.ordwen.odailyquests.files.implementations;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.enums.QuestsMessages;
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

        boolean missingMessages = false;
        for (QuestsMessages item : QuestsMessages.values()) {
            if (config.getString(item.getPath()) == null) {
                missingMessages = true;
                config.set(item.getPath(), item.getDefault());
            }
        }

        if (missingMessages) {
            try {
                config.save(file);
            } catch (Exception e) {
                PluginLogger.error("An error occurred while saving the messages file.");
                PluginLogger.error(e.getMessage());
            }

            PluginLogger.warn("Some messages are missing in the messages file. Default messages have been added.");
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