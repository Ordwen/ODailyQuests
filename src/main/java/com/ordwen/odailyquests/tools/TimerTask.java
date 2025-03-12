package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.QuestLoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerTask {

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Set a runnable to reload quests at midnight.
     * @param start date and time to start the task.
     */
    public TimerTask(LocalDateTime start) {
        final LocalDateTime nextDay = start.plusDays(1).truncatedTo(ChronoUnit.DAYS).plusSeconds(2);
        final long initialDelay = Duration.between(start, nextDay).toNanos();

        scheduler.schedule(this::executeAndReschedule, initialDelay, TimeUnit.NANOSECONDS);
    }

    private void executeAndReschedule() {
        PluginLogger.info("It's a new day. The player quests are being reloaded.");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            final String msg = QuestsMessages.NEW_DAY.toString();
            if (!msg.isEmpty()) player.sendMessage(msg);

            int totalAchievedQuests = QuestsManager.getActiveQuests().get(player.getName()).getTotalAchievedQuests();
            QuestLoaderUtils.loadNewPlayerQuests(player.getName(), QuestsManager.getActiveQuests(), totalAchievedQuests);
        }

        final long delayUntilNextRun = Duration.ofDays(1).toNanos();
        scheduler.schedule(this::executeAndReschedule, delayUntilNextRun, TimeUnit.NANOSECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}
