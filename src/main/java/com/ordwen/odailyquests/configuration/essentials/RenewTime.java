package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RenewTime implements IConfigurable {

    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("h:mma"), // 3:15AM
            DateTimeFormatter.ofPattern("ha"),    // 3AM
            DateTimeFormatter.ofPattern("H:mm"),  // 14:30
            DateTimeFormatter.ofPattern("H")      // 14
    );

    private static final String DEFAULT_TIME = "00:00";
    private static final String DEFAULT_ZONE = "SystemDefault";

    private final ConfigurationFile configurationFile;
    private LocalTime time;
    private ZoneId zoneId;

    public RenewTime(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final String timeStr = configurationFile.getConfig().getString("renew_time", DEFAULT_TIME);
        final String zoneStr = configurationFile.getConfig().getString("renew_time_zone", DEFAULT_ZONE);

        try {
            time = parseTime(timeStr);
            zoneId = parseZone(zoneStr);
        } catch (IllegalArgumentException e) {
            PluginLogger.warn("Invalid time format in config: " + timeStr + ". Using default: " + DEFAULT_TIME);
            time = parseTime(DEFAULT_TIME);
        }
    }

    /**
     * Parses a time string (e.g., "3AM", "1:45PM", "14", "14:30").
     *
     * @param time the input time string.
     * @return LocalTime representing the parsed time.
     * @throws IllegalArgumentException if the format is invalid.
     */
    private LocalTime parseTime(String time) {
        time = time.toUpperCase().trim();

        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalTime.parse(time, formatter);
            } catch (DateTimeParseException ignored) {
                // we try the next formatter
            }
        }

        throw new IllegalArgumentException("Invalid time format: " + time);
    }

    /**
     * Parses a zone string (e.g., "UTC", "America/New_York").
     *
     * @param zone the input zone string.
     * @return ZoneId representing the parsed zone.
     */
    private ZoneId parseZone(String zone) {
        if (zone.equalsIgnoreCase(DEFAULT_ZONE)) {
            return ZoneId.systemDefault();
        }

        try {
            return ZoneId.of(zone);
        } catch (DateTimeException e) {
            PluginLogger.warn("Invalid zone format in config: " + zone + ". Using system default.");
            return ZoneId.systemDefault();
        }
    }

    private static RenewTime getInstance() {
        return ConfigFactory.getConfig(RenewTime.class);
    }

    public static LocalTime getRenewTime() {
        return getInstance().time;
    }

    public static ZoneId getZoneId() {
        return getInstance().zoneId;
    }
}
