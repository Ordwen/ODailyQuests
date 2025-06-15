package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.enums.ProgressionMessageType;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.PluginLogger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ProgressionMessage implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public ProgressionMessage(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private boolean isEnabled;
    private String message;
    private ProgressionMessageType progressionMessageType;

    private BarColor barColor;
    private BarStyle barStyle;

    private final Map<Player, BossBar> currentBossBars = new HashMap<>();

    @Override
    public void load() {
        isEnabled = configurationFile.getConfig().getBoolean("progression_message.enabled");

        if (!isEnabled) {
            PluginLogger.info("Progression message is disabled.");
            return;
        }

        message = configurationFile.getConfig().getString("progression_message.text");

        if (message == null) {
            PluginLogger.error("Progression message is null, disabling progression message.");
            PluginLogger.error("Please set a valid message in the configuration file (progression_message.text).");
            isEnabled = false;
        }

        final String type = configurationFile.getConfig().getString("progression_message.type");
        if (type == null) {
            PluginLogger.warn("Progression message type is null, defaulting to CHAT.");
            progressionMessageType = ProgressionMessageType.CHAT;
            return;
        }

        try {
            progressionMessageType = ProgressionMessageType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            PluginLogger.warn("Progression message type is invalid, defaulting to CHAT.");
            progressionMessageType = ProgressionMessageType.CHAT;
        }

        if (progressionMessageType == ProgressionMessageType.BOSSBAR) {
            loadBossBar();
        }
    }

    private void loadBossBar() {
        final String color = configurationFile.getConfig().getString("progression_message.bossbar.color");
        final String style = configurationFile.getConfig().getString("progression_message.bossbar.style");

        if (color == null) {
            PluginLogger.warn("Progression message bossbar color is null, defaulting to BLUE.");
            barColor = BarColor.BLUE;
        } else {
            try {
                barColor = BarColor.valueOf(color.toUpperCase());
            } catch (IllegalArgumentException e) {
                PluginLogger.warn("Progression message bossbar color is invalid, defaulting to BLUE.");
                barColor = BarColor.BLUE;
            }
        }

        if (style == null) {
            PluginLogger.warn("Progression message bossbar style is null, defaulting to SOLID.");
            barStyle = BarStyle.SOLID;
        } else {
            try {
                barStyle = BarStyle.valueOf(style.toUpperCase());
            } catch (IllegalArgumentException e) {
                PluginLogger.warn("Progression message bossbar style is invalid, defaulting to SOLID.");
                barStyle = BarStyle.SOLID;
            }
        }
    }

    /**
     * Send progression message.
     *
     * @param player      to send.
     * @param questName   name of the achieved quest.
     * @param progression progression of the quest.
     * @param required    required progression of the quest.
     */
    public void sendProgressionMessageInternal(Player player, String questName, int progression, int required) {
        if (isEnabled) {
            final String parsedQuestName = TextFormatter.format(player, questName);

            final String toSend = TextFormatter.format(player, message
                    .replace("%player%", player.getDisplayName())
                    .replace("%questName%", parsedQuestName)
                    .replace("%progress%", String.valueOf(progression))
                    .replace("%required%", String.valueOf(required))
                    .replace("%progressBar%", ProgressBar.getProgressBar(progression, required))
            );

            switch (progressionMessageType) {
                case ACTIONBAR -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(toSend));
                case CHAT -> player.sendMessage(toSend);
                case BOSSBAR -> currentBossBars.computeIfAbsent(player, p -> {
                    final BossBar bossBar = Bukkit.getServer().createBossBar(toSend, barColor, barStyle);
                    bossBar.addPlayer(p);
                    ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(p).runDelayed(() -> removeBossBar(p), null, 100L);
                    return bossBar;
                }).setTitle(toSend);
            }
        }
    }

    /**
     * Remove boss bar from player.
     *
     * @param player to remove boss bar from.
     */
    private void removeBossBarInternal(Player player) {
        if (currentBossBars.containsKey(player)) {
            currentBossBars.get(player).removePlayer(player);
            currentBossBars.remove(player);
        }
    }

    private static ProgressionMessage getInstance() {
        return ConfigFactory.getConfig(ProgressionMessage.class);
    }

    public static void sendProgressionMessage(Player player, String questName, int progression, int required) {
        getInstance().sendProgressionMessageInternal(player, questName, progression, required);
    }

    public static void removeBossBar(Player player) {
        getInstance().removeBossBarInternal(player);
    }
}
