package com.ordwen.odailyquests.quests.categories;

import com.ordwen.odailyquests.tools.RenewSchedule;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Represents a group of quest categories that share the same renewal schedule.
 * <p>
 * Each group has its own temporal settings (renew time, interval, timezone),
 * allowing different categories to renew at different times (e.g., daily vs weekly).
 */
public class CategoryGroup {

    private final String name;
    private final List<String> categoryNames;
    private final LocalTime renewTime;
    private final Duration renewInterval;
    private final ZoneId zoneId;

    /**
     * Constructs a new CategoryGroup.
     *
     * @param name          the unique name of the group (e.g., "daily", "weekly")
     * @param categoryNames the names of categories belonging to this group
     * @param renewTime     the time of day when quests renew
     * @param renewInterval the interval between renewals
     * @param zoneId        the timezone for renewal calculations
     */
    public CategoryGroup(String name, List<String> categoryNames, LocalTime renewTime, Duration renewInterval, ZoneId zoneId) {
        this.name = name;
        this.categoryNames = List.copyOf(categoryNames);
        this.renewTime = renewTime;
        this.renewInterval = renewInterval;
        this.zoneId = zoneId;
    }

    /**
     * Gets the name of this group.
     *
     * @return the group name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of category names in this group.
     *
     * @return an immutable list of category names
     */
    public List<String> getCategoryNames() {
        return categoryNames;
    }

    /**
     * Gets the renewal time for this group.
     *
     * @return the local time of renewal
     */
    public LocalTime getRenewTime() {
        return renewTime;
    }

    /**
     * Gets the renewal interval for this group.
     *
     * @return the duration between renewals
     */
    public Duration getRenewInterval() {
        return renewInterval;
    }

    /**
     * Gets the timezone for this group.
     *
     * @return the zone ID
     */
    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * Checks if a category belongs to this group.
     *
     * @param categoryName the name of the category to check
     * @return true if the category is part of this group
     */
    public boolean containsCategory(String categoryName) {
        return categoryNames.stream()
                .anyMatch(name -> name.equalsIgnoreCase(categoryName));
    }

    /**
     * Creates a {@link RenewSchedule.Settings} instance for this group.
     * <p>
     * This allows using the existing RenewSchedule utility methods
     * with group-specific temporal settings.
     *
     * @param timestampMode the timestamp mode to use
     * @return the schedule settings for this group
     */
    public RenewSchedule.Settings toScheduleSettings(int timestampMode) {
        return new RenewSchedule.Settings(renewTime, renewInterval, zoneId, timestampMode);
    }
}
