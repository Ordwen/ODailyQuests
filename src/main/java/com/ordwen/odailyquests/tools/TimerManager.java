package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.configuration.essentials.CategoryGroupsLoader;
import com.ordwen.odailyquests.quests.categories.CategoryGroup;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Manages multiple GroupTimerTask instances, one for each category group.
 * <p>
 * This class provides a centralized way to start, stop, and reload all timers
 * associated with different category groups, each with their own renewal schedule.
 */
public class TimerManager {

    private final Map<String, GroupTimerTask> timers = new HashMap<>();
    private final ScheduledExecutorService scheduler;

    /**
     * Creates a new TimerManager with a shared thread pool.
     */
    public TimerManager() {
        // Use a shared thread pool for all timers to avoid creating too many threads
        this.scheduler = Executors.newScheduledThreadPool(
                Math.max(1, CategoryGroupsLoader.getGroupNames().size())
        );
    }

    /**
     * Starts all timers for configured category groups.
     *
     * @param start the starting date/time for scheduling
     */
    public void start(LocalDateTime start) {
        PluginLogger.info("Starting timer manager with " + CategoryGroupsLoader.getGroupNames().size() + " group(s).");

        for (Map.Entry<String, CategoryGroup> entry : CategoryGroupsLoader.getAllGroups().entrySet()) {
            final String groupName = entry.getKey();
            final CategoryGroup group = entry.getValue();

            final GroupTimerTask timer = new GroupTimerTask(group, scheduler);
            timer.scheduleNextExecution(start);
            timers.put(groupName, timer);

            PluginLogger.info("Started timer for group '" + groupName + "'.");
        }
    }

    /**
     * Reloads all timers with the current configuration.
     * Cancels existing timers and creates new ones.
     */
    public void reload() {
        PluginLogger.info("Reloading timer manager...");

        // Cancel all existing timers
        for (GroupTimerTask timer : timers.values()) {
            timer.cancel();
        }
        timers.clear();

        // Start new timers based on current configuration
        start(LocalDateTime.now());
    }

    /**
     * Stops all timers and shuts down the scheduler.
     */
    public void stop() {
        PluginLogger.info("Stopping timer manager...");

        for (GroupTimerTask timer : timers.values()) {
            timer.cancel();
        }
        timers.clear();

        scheduler.shutdownNow();
    }

    /**
     * Gets the timer for a specific group.
     *
     * @param groupName the name of the group
     * @return the GroupTimerTask, or null if not found
     */
    public GroupTimerTask getTimerForGroup(String groupName) {
        return timers.get(groupName);
    }

    /**
     * Gets all active timers.
     *
     * @return a map of group name to timer
     */
    public Map<String, GroupTimerTask> getAllTimers() {
        return new HashMap<>(timers);
    }
}
