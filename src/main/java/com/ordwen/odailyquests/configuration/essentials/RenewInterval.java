package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RenewInterval implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public RenewInterval(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private Duration interval;

    private String days;
    private String hours;
    private String minutes;
    private String fewSeconds;

    /**
     * Load all temporality settings.
     */
    @Override
    public void load() {
        final ConfigurationSection config = configurationFile.getConfig();

        final String intervalStr = config.getString("renew_interval", "1d");
        this.interval = parseDuration(intervalStr);

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

    private Duration parseDuration(String interval) {
        final Pattern pattern = Pattern.compile("(\\d+)([dhm])");
        final Matcher matcher = pattern.matcher(interval);
        Duration duration = Duration.ZERO;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            duration = switch (unit) {
                case "d" -> duration.plus(value, ChronoUnit.DAYS);
                case "h" -> duration.plus(value, ChronoUnit.HOURS);
                case "m" -> duration.plus(value, ChronoUnit.MINUTES);
                default -> duration;
            };
        }

        return duration;
    }

    private static RenewInterval getInstance() {
        return ConfigFactory.getConfig(RenewInterval.class);
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

    public static Duration getRenewInterval() {
        return getInstance().interval;
    }
}
