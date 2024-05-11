package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.TimerTask;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;

public class Temporality {

    private final ConfigurationFiles configurationFiles;

    public Temporality(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    private static String d;
    private static String h;
    private static String m;
    private static String fewSeconds;

    /**
     * Load all temporality settings.
     */
    public void loadTemporalitySettings() {
        final ConfigurationSection config = configurationFiles.getConfigFile();
        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            questSystem.setTemporalityMode(config.getInt(questSystem.getConfigPath() + "temporality_mode"));
        });

        final ConfigurationSection initials = config.getConfigurationSection("temporality_initials");

        d = initials.getString("days");
        h = initials.getString("hours");
        m = initials.getString("minutes");
        fewSeconds = initials.getString("few_seconds");
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
