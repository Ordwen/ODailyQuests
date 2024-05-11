package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.QuestSystem;
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
    QuestSystem questSystem;

    final Runnable runnable = () -> {
        PluginLogger.fine("It's a new day. The player quests are being reloaded.");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {

            final String msg = QuestsMessages.NEW_DAY.toString();
            if (msg != null) player.sendMessage(msg);

            int totalAchievedQuests = questSystem.getActiveQuests().get(player.getName()).getTotalAchievedQuests();
            QuestLoaderUtils.loadNewPlayerQuests(questSystem, player.getName(), questSystem.getActiveQuests(), totalAchievedQuests);
        }
    };

    /**
     * Set a runnable to reload quests at midnight.
     * @param start date and time to start the task.
     */
    public TimerTask(LocalDateTime start, QuestSystem questSystem) {
        final LocalDateTime end = start.plusDays(1).truncatedTo(ChronoUnit.DAYS);
        final Duration duration = Duration.between(start, end);

        scheduler.scheduleAtFixedRate(runnable, duration.toMillis(), 86400000, TimeUnit.MILLISECONDS);
        this.questSystem = questSystem;
    }

    public void stop() {
        scheduler.shutdown();
    }
}
