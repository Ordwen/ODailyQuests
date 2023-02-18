package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;
import org.bukkit.configuration.ConfigurationSection;

public class Temporality {

    private final ConfigurationFiles configurationFiles;

    public Temporality(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static int temporalityMode;
    private static String d;
    private static String h;
    private static String m;
    private static String fewSeconds;

    /**
     * Load all temporality settings.
     */
    public void loadTemporalitySettings() {
        final ConfigurationSection config = configurationFiles.getConfigFile();
        temporalityMode = config.getInt("temporality_mode");

        final ConfigurationSection initials = config.getConfigurationSection("temporality_initials");

        d = initials.getString("days");
        h = initials.getString("hours");
        m = initials.getString("minutes");
        fewSeconds = initials.getString("few_seconds");
    }

    /**
     * Get temporality mode.
     * @return plugin mode.
     */
    public static int getTemporalityMode() {
        return temporalityMode;
    }

    /**
     * Get day initial.
     * @return d
     */
    public static String getDayInitial() {
        return d;
    }

    /**
     * Get hour initial.
     * @return h
     */
    public static String getHourInitial() {
        return h;
    }

    /**
     * Get minute initial.
     * @return m
     */
    public static String getMinuteInitial() {
        return m;
    }

    /**
     * Get few seconds text.
     * @return fewSeconds
     */
    public static String getFewSeconds() {
        return fewSeconds;
    }
}
