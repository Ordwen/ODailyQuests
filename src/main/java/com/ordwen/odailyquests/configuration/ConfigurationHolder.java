package com.ordwen.odailyquests.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import com.ordwen.odailyquests.ODailyQuests;

/**
 * I thought it would be good to have a constant class for config management.
 * For now, I just added an arrangement for the prefix, I can add more if you want.
 */
public class ConfigurationHolder {

    private static final FileConfiguration cfg = ODailyQuests.INSTANCE.getConfigurationFiles().getConfigFile();

    public static class DatabaseConfig {
        public static final String prefix = cfg.getString("database.table_prefix");
        public static String playerTableName = prefix + "player";
        public static String progressionTableName = prefix + "progression";
        
        

    }
}
