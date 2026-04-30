package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.configuration.essentials.CategoryGroupsLoader;
import com.ordwen.odailyquests.configuration.essentials.RenewInterval;
import com.ordwen.odailyquests.configuration.essentials.TimestampMode;
import com.ordwen.odailyquests.quests.categories.CategoryGroup;

import java.time.*;

public class TimeRemain {

    private TimeRemain() {
    }

    /**
     * Get the time remaining before the next quests draw (same schedule as TimerTask).
     *
     * @param playerName player to consider.
     * @return the time remaining as a formatted String.
     */
    public static String timeRemain(String playerName) {
        long restMillis;

        final Duration renewInterval = RenewInterval.getRenewInterval();
        if (renewInterval == null || renewInterval.isZero() || renewInterval.isNegative()) {
            return formatTimeRemain(0);
        }

        if (TimestampMode.getTimestampMode() == 1) {
            final RenewSchedule.Settings s = RenewSchedule.settings();
            if (!RenewSchedule.isValid(s)) return formatTimeRemain(0);

            final ZonedDateTime now = ZonedDateTime.now(s.zone());
            restMillis = RenewSchedule.millisUntilNext(now, s);
        } else {
            final long timestamp = ODailyQuestsAPI.getPlayerQuests(playerName).getTimestamp();
            restMillis = (timestamp + renewInterval.toMillis()) - System.currentTimeMillis();
            restMillis = Math.max(0L, restMillis);
        }

        return formatTimeRemain(restMillis);
    }

    /**
     * Get the time remaining before the next quests draw for a specific category group.
     *
     * @param playerName player to consider.
     * @param groupName  the name of the category group.
     * @return the time remaining as a formatted String, or null if group not found.
     */
    public static String timeRemainForGroup(String playerName, String groupName) {
        final CategoryGroup group = CategoryGroupsLoader.getGroup(groupName);
        if (group == null) {
            return null;
        }

        long restMillis;

        final Duration renewInterval = group.getRenewInterval();
        if (renewInterval == null || renewInterval.isZero() || renewInterval.isNegative()) {
            return formatTimeRemain(0);
        }

        if (TimestampMode.getTimestampMode() == 1) {
            final RenewSchedule.Settings s = group.toScheduleSettings(TimestampMode.getTimestampMode());
            if (!RenewSchedule.isValid(s)) return formatTimeRemain(0);

            final ZonedDateTime now = ZonedDateTime.now(s.zone());
            restMillis = RenewSchedule.millisUntilNext(now, s);
        } else {
            final long timestamp = ODailyQuestsAPI.getPlayerQuests(playerName).getTimestamp(groupName);
            restMillis = (timestamp + renewInterval.toMillis()) - System.currentTimeMillis();
            restMillis = Math.max(0L, restMillis);
        }

        return formatTimeRemain(restMillis);
    }

    private static String formatTimeRemain(long rest) {
        final String d = RenewInterval.getDayInitial();
        final String h = RenewInterval.getHourInitial();
        final String m = RenewInterval.getMinuteInitial();

        final int days = (int) (rest / (1000L * 60 * 60 * 24));
        final int hours = (int) ((rest / (1000L * 60 * 60)) % 24);
        final int minutes = (int) ((rest / (1000L * 60)) % 60);

        if (days != 0) return String.format("%d%s%d%s%d%s", days, d, hours, h, minutes, m);
        if (hours != 0) return String.format("%d%s%d%s", hours, h, minutes, m);
        if (minutes != 0) return String.format("%d%s", minutes, m);
        return RenewInterval.getFewSeconds();
    }
}
