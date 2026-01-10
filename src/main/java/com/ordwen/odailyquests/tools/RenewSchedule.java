package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.configuration.essentials.RenewInterval;
import com.ordwen.odailyquests.configuration.essentials.RenewTime;
import com.ordwen.odailyquests.configuration.essentials.TimestampMode;

import java.time.*;

public final class RenewSchedule {

    private RenewSchedule() {
    }

    /**
     * Small container to pass around schedule settings cleanly.
     */
    public record Settings(LocalTime renewTime, Duration interval, ZoneId zone, int mode) {
    }

    public static Settings settings() {
        return new Settings(
                RenewTime.getRenewTime(),
                RenewInterval.getRenewInterval(),
                RenewTime.getZoneId(),
                TimestampMode.getTimestampMode()
        );
    }

    public static boolean isValid(Settings s) {
        return s.interval() != null && !s.interval().isZero() && !s.interval().isNegative()
                && s.renewTime() != null
                && s.zone() != null;
    }

    /**
     * Returns the next scheduled execution >= now, using anchor = today at renewTime, then + k*interval.
     * This is the single source of truth used by TimerTask & TimeRemain.
     */
    public static ZonedDateTime nextExecutionAtOrAfter(ZonedDateTime now, Settings s) {
        ZonedDateTime anchor = now.with(s.renewTime());

        if (anchor.isAfter(now) || anchor.isEqual(now)) {
            return anchor;
        }

        long intervalMillis = s.interval().toMillis();
        long elapsedMillis = Duration.between(anchor, now).toMillis();

        long k = (elapsedMillis / intervalMillis) + 1;
        return anchor.plus(s.interval().multipliedBy(k));
    }

    /**
     * Returns true if at least one scheduled execution time happened AFTER lastRenew and <= now.
     * This is what you want for "offline player comes back" behavior.
     */
    public static boolean shouldRenewSince(ZonedDateTime lastRenew, ZonedDateTime now, Settings s) {
        // Next tick strictly after lastRenew
        ZonedDateTime nextAfterLast = nextExecutionAtOrAfter(lastRenew, s);
        if (nextAfterLast.isEqual(lastRenew)) {
            nextAfterLast = nextAfterLast.plus(s.interval());
        }
        return !nextAfterLast.isAfter(now); // nextAfterLast <= now
    }

    public static long millisUntilNext(ZonedDateTime now, Settings s) {
        ZonedDateTime next = nextExecutionAtOrAfter(now, s);
        return Math.max(0L, Duration.between(now, next).toMillis());
    }
}
