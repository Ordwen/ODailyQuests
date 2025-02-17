package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

public class Temporality implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public Temporality(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private int temporalityMode;
    private String days;
    private String hours;
    private String minutes;
    private String fewSeconds;

    /**
     * Load all temporality settings.
     */
    @Override
    public void load() {
        final ConfigurationSection config = configurationFile.getConfigFile();
        temporalityMode = config.getInt("temporality_mode");

        final ConfigurationSection initials = config.getConfigurationSection("temporality_initials");

        if (initials == null) {
            PluginLogger.error("Temporality initials are not set in the configuration file.");
            PluginLogger.error("Default values will be used.");

            days = "d";
            hours = "h";
            minutes = "m";
            fewSeconds = "few seconds";

            return;
        }

        days = initials.getString("days");
        hours = initials.getString("hours");
        minutes = initials.getString("minutes");
        fewSeconds = initials.getString("few_seconds");
    }

    private static Temporality getInstance() {
        return ConfigFactory.getConfig(Temporality.class);
    }

    public static int getTemporalityMode() {
        return getInstance().temporalityMode;
    }

    public static String getDayInitial() {
        return getInstance().days;
    }

    public static String getHourInitial() {
        return getInstance().hours;
    }

    public static String getMinuteInitial() {
        return getInstance().minutes;
    }

    public static String getFewSeconds() {
        return getInstance().fewSeconds;
    }
}
