package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.files.ConfigurationFiles;

public class Temporality {

    private final ConfigurationFiles configurationFiles;

    public Temporality(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static int temporalityMode;
    private static String d;
    private static String h;
    private static String m;

    /**
     * Load all temporality settings.
     */
    public void loadTemporalitySettings() {
        temporalityMode = configurationFiles.getConfigFile().getInt("temporality_mode");

        d = configurationFiles.getConfigFile().getConfigurationSection("temporality_initials").getString("days");
        h = configurationFiles.getConfigFile().getConfigurationSection("temporality_initials").getString("hours");
        m = configurationFiles.getConfigFile().getConfigurationSection("temporality_initials").getString("minutes");
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
}
