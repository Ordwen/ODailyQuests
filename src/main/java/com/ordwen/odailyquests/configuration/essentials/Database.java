package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

public class Database implements IConfigurable {

    private final ConfigurationFile configurationFile;

    private StorageMode mode;

    private String name;
    private String host;
    private String port;
    private String user;
    private String password;

    public Database(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final String expectedMode = configurationFile.getConfig().getString("storage_mode");
        if (expectedMode == null) {
            PluginLogger.error("storage_mode is not defined in the configuration file.");
            throw new IllegalArgumentException("storage_mode is not defined in the configuration file.");
        }

        mode = StorageMode.getStorageMode(expectedMode);
        if (mode.isLocal()) return;

        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("database");
        if (section == null) {
            PluginLogger.error("Database section is not defined in the configuration file.");
            throw new IllegalArgumentException("Database section is not defined in the configuration file.");
        }

        name = section.getString("name");
        host = section.getString("host");
        user = section.getString("user");
        password = section.getString("password");
        port = section.getString("port");
    }

    private static Database getInstance() {
        return ConfigFactory.getConfig(Database.class);
    }

    public static StorageMode getMode() {
        return getInstance().mode;
    }

    public static String getName() {
        return getInstance().name;
    }

    public static String getHost() {
        return getInstance().host;
    }

    public static String getUser() {
        return getInstance().user;
    }

    public static String getPassword() {
        return getInstance().password;
    }

    public static String getPort() {
        return getInstance().port;
    }
}
