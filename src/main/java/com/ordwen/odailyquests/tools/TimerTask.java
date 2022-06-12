package com.ordwen.odailyquests.tools;

import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.enums.QuestsMessages;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerTask {

    public TimerTask(LocalDateTime start) {

        Runnable runnable = () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.sendMessage(QuestsMessages.NEW_DAY.toString());
                Utils.loadNewPlayerQuests(player.getName(), QuestsManager.getActiveQuests(), Modes.getTimestampMode(), new LinkedHashMap<>());
            }
        };

        LocalDateTime end = start.plusDays(1).truncatedTo(ChronoUnit.DAYS);

        Duration duration = Duration.between(start, end);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, duration.toMillis(), 86400000, TimeUnit.MILLISECONDS);
    }
}
