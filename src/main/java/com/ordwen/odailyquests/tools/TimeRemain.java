package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.configuration.essentials.TimestampMode;
import com.ordwen.odailyquests.configuration.essentials.RenewInterval;
import com.ordwen.odailyquests.configuration.essentials.RenewTime;

import java.time.*;

public class TimeRemain {

    private TimeRemain() {
    }

    /**
     * Get the time remain before the next quests draw.
     *
     * @param playerName player to consider.
     * @return the time remain before the next quests draw, in String.
     */
    public static String timeRemain(String playerName) {
        long timestamp = ODailyQuestsAPI.getPlayerQuests(playerName).getTimestamp();
        long rest;

        if (TimestampMode.getTimestampMode() == 1) {
            final LocalTime renewTime = RenewTime.getRenewTime();
            final ZoneId zone = RenewTime.getZoneId();

            final ZonedDateTime lastRenew = Instant.ofEpochMilli(timestamp).atZone(zone);
            final ZonedDateTime now = ZonedDateTime.now(zone);
            ZonedDateTime nextRenew = lastRenew.toLocalDate().plusDays(1).atTime(renewTime).atZone(zone);

            if (nextRenew.isBefore(now)) {
                nextRenew = nextRenew.plusDays(1);
            }

            rest = Duration.between(now, nextRenew).toMillis();
        } else {
            Duration renewDuration = RenewInterval.getRenewInterval();
            rest = (renewDuration != null) ? (timestamp + renewDuration.toMillis()) - System.currentTimeMillis() : 0;
        }

        return formatTimeRemain(rest);
    }

    /**
     * Get a text from remaining time.
     *
     * @param rest remaining time in milliseconds.
     * @return formatted text with remaining time.
     */
    private static String formatTimeRemain(long rest) {
        final String d = RenewInterval.getDayInitial();
        final String h = RenewInterval.getHourInitial();
        final String m = RenewInterval.getMinuteInitial();

        final int days = (int) (rest / (1000 * 60 * 60 * 24));
        final int hours = (int) ((rest / (1000 * 60 * 60)) % 24);
        final int minutes = (int) ((rest / (1000 * 60)) % 60);

        final String timeRemain;

        if (days != 0) timeRemain = String.format("%d%s%d%s%d%s", days, d, hours, h, minutes, m);
        else if (hours != 0) timeRemain = String.format("%d%s%d%s", hours, h, minutes, m);
        else if (minutes != 0) timeRemain = String.format("%d%s", minutes, m);
        else timeRemain = RenewInterval.getFewSeconds();

        return timeRemain;
    }
}
