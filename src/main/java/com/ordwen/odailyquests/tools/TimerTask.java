package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.api.ODailyQuestsAPI;
import com.ordwen.odailyquests.configuration.essentials.RenewInterval;
import com.ordwen.odailyquests.configuration.essentials.RenewTime;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.*;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimerTask {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledTask;

    /**
     * Set a runnable to reload quests at midnight.
     *
     * @param start date and time to start the task.
     */
    public TimerTask(LocalDateTime start) {
        scheduleNextExecution(start);
    }

    private void scheduleNextExecution(LocalDateTime start) {
        final LocalTime renewTime = RenewTime.getRenewTime();
        final Duration renewInterval = RenewInterval.getRenewInterval();
        final ZoneId renewZoneId = RenewTime.getZoneId();

        final ZonedDateTime zonedStart = start.atZone(ZoneId.systemDefault()).withZoneSameInstant(renewZoneId);
        ZonedDateTime nextExecution = zonedStart.with(renewTime);

        // add the interval until the next execution is after the start time
        while (nextExecution.isBefore(zonedStart)) {
            nextExecution = nextExecution.plus(renewInterval);
        }

        final long initialDelay = Duration.between(ZonedDateTime.now(ZoneId.systemDefault()), nextExecution.withZoneSameInstant(ZoneId.systemDefault())).toNanos();

        scheduledTask = scheduler.schedule(this::executeAndReschedule, initialDelay, TimeUnit.NANOSECONDS);
    }

    private void executeAndReschedule() {
        PluginLogger.info("It's a new day. The player quests are being reloaded.");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            final String msg = QuestsMessages.NEW_DAY.toString();
            if (!msg.isEmpty()) player.sendMessage(msg);

            final PlayerQuests playerQuests = ODailyQuestsAPI.getPlayerQuests(player.getName());
            final int totalAchievedQuests = playerQuests.getTotalAchievedQuests();
            final Map<String, Integer> totalAchievedQuestsByCategory = playerQuests.getTotalAchievedQuestsByCategory();
            QuestLoaderUtils.loadNewPlayerQuests(player.getName(), QuestsManager.getActiveQuests(), totalAchievedQuestsByCategory, totalAchievedQuests);
        }

        scheduleNextExecution(LocalDateTime.now());
    }

    public void reload() {
       cancel();
        scheduleNextExecution(LocalDateTime.now());
    }

    private void cancel() {
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
        }
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
