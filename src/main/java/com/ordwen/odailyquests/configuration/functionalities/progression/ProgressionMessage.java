package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.enums.ProgressionMessageType;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.ColorConvert;
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

public class ProgressionMessage {

    private final ConfigurationFiles configurationFiles;

    public ProgressionMessage(ConfigurationFiles configurationFiles) {
        this.configurationFiles = configurationFiles;
    }

    /**
     * Init variables
     */
    private static boolean isEnabled;
    private static String message;
    private static ProgressionMessageType progressionMessageType;

    private static BarColor barColor;
    private static BarStyle barStyle;

    private static final Map<Player, BossBar> currentBossBars = new HashMap<>();

    /**
     * Load configuration section.
     */
    public void loadProgressionMessage() {
        isEnabled = configurationFiles.getConfigFile().getBoolean("progression_message.enabled");

        if (!isEnabled) {
            PluginLogger.info("Progression message is disabled.");
            return;
        }

        message = configurationFiles.getConfigFile().getString("progression_message.text");

        if (message == null) {
            PluginLogger.error("Progression message is null, disabling progression message.");
            PluginLogger.error("Please set a valid message in the configuration file (progression_message.text).");
            isEnabled = false;
        }

        final String type = configurationFiles.getConfigFile().getString("progression_message.type");
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
            final String color = configurationFiles.getConfigFile().getString("progression_message.bossbar.color");
            final String style = configurationFiles.getConfigFile().getString("progression_message.bossbar.style");

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
    }

    /**
     * Send progression message.
     * @param player to send.
     * @param questName name of the achieved quest.
     * @param progression progression of the quest.
     * @param required required progression of the quest.
     */
    public static void sendProgressionMessage(Player player, String questName, int progression, int required) {
        if (isEnabled) {
            final String toSend = ColorConvert.convertColorCode(message
                    .replace("%player%", player.getDisplayName())
                    .replace("%questName%", questName)
                    .replace("%progress%", String.valueOf(progression))
                    .replace("%required%", String.valueOf(required))
                    .replace("%progressBar%", ProgressBar.getProgressBar(progression, required))
            );

            switch (progressionMessageType) {
                case ACTIONBAR -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(toSend));
                case CHAT -> player.sendMessage(toSend);
                case BOSSBAR -> {
                    if (currentBossBars.containsKey(player)) {
                        currentBossBars.get(player).setTitle(toSend);
                        return;
                    }

                    final BossBar bossBar = Bukkit.getServer().createBossBar(toSend, barColor, barStyle);
                    bossBar.addPlayer(player);
                    currentBossBars.put(player, bossBar);

                    ODailyQuests.morePaperLib.scheduling().entitySpecificScheduler(player).runDelayed(() -> removeBossBar(player), null, 100L);
                }
            }
        }
    }

    /**
     * Remove boss bar from player.
     * @param player to remove boss bar from.
     */
    private static void removeBossBar(Player player) {
        if (currentBossBars.containsKey(player)) {
            currentBossBars.get(player).removePlayer(player);
            currentBossBars.remove(player);
        }
    }
}
