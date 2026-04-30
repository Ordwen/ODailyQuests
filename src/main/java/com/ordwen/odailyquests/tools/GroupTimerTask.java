package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.configuration.essentials.TimestampMode;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.categories.CategoryGroup;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.*;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Timer task for a specific category group.
 * <p>
 * Each group has its own timer that handles renewal at the configured interval.
 * When the timer fires, only the quests belonging to this group's categories are renewed.
 */
public class GroupTimerTask {

    private final CategoryGroup group;
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledTask;

    /**
     * Creates a new GroupTimerTask for the specified group.
     *
     * @param group     the category group this timer manages
     * @param scheduler the shared scheduler to use
     */
    public GroupTimerTask(CategoryGroup group, ScheduledExecutorService scheduler) {
        this.group = group;
        this.scheduler = scheduler;
    }

    /**
     * Schedules the next execution based on the group's temporal settings.
     *
     * @param start the starting date/time for calculation
     */
    public void scheduleNextExecution(LocalDateTime start) {
        final RenewSchedule.Settings settings = group.toScheduleSettings(TimestampMode.getTimestampMode());

        if (!RenewSchedule.isValid(settings)) {
            PluginLogger.error("Invalid renew schedule for group '" + group.getName() + "'. Timer not scheduled.");
            return;
        }

        final ZonedDateTime now = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(settings.zone());
        final ZonedDateTime next = RenewSchedule.nextExecutionAtOrAfter(now, settings);

        long initialDelayNanos = Duration.between(
                ZonedDateTime.now(ZoneId.systemDefault()),
                next.withZoneSameInstant(ZoneId.systemDefault())
        ).toNanos();

        scheduledTask = scheduler.schedule(this::executeAndReschedule, initialDelayNanos, TimeUnit.NANOSECONDS);

        PluginLogger.info("Group '" + group.getName() + "' scheduled for renewal at " + next + ".");
    }

    /**
     * Executes the renewal for this group and reschedules the next execution.
     */
    private void executeAndReschedule() {
        PluginLogger.info("Renewing quests for group '" + group.getName() + "'.");

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            renewPlayerQuestsForGroup(player);
        }

        scheduleNextExecution(LocalDateTime.now());
    }

    /**
     * Renews a player's quests for this group only.
     *
     * @param player the player whose quests to renew
     */
    private void renewPlayerQuestsForGroup(Player player) {
        // Send renewal message to player
        final String msg = QuestsMessages.NEW_DAY.toString();
        if (msg != null) {
            final String formattedMsg = msg.replace("%group%", group.getName());
            player.sendMessage(formattedMsg);
        }

        final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(player.getName());
        if (playerQuests == null) {
            PluginLogger.warn("Could not find quests for player " + player.getName() + " during group renewal.");
            return;
        }

        // Get current totals to preserve
        final int totalAchievedQuests = playerQuests.getTotalAchievedQuests();
        final Map<String, Integer> totalAchievedQuestsByCategory = playerQuests.getTotalAchievedQuestsByCategory();

        // Renew only quests for this group
        QuestLoaderUtils.loadNewPlayerQuestsForGroup(
                player.getName(),
                QuestsManager.getActiveQuests(),
                totalAchievedQuestsByCategory,
                totalAchievedQuests,
                group
        );
    }

    /**
     * Cancels this timer.
     */
    public void cancel() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }

    /**
     * Gets the category group this timer manages.
     *
     * @return the category group
     */
    public CategoryGroup getGroup() {
        return group;
    }

    /**
     * Gets the next scheduled execution time.
     *
     * @return the next execution time, or null if not scheduled
     */
    public ZonedDateTime getNextExecution() {
        final RenewSchedule.Settings settings = group.toScheduleSettings(TimestampMode.getTimestampMode());
        if (!RenewSchedule.isValid(settings)) {
            return null;
        }
        return RenewSchedule.nextExecutionAtOrAfter(ZonedDateTime.now(settings.zone()), settings);
    }
}
