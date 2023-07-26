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

    private static final Map<Player, BossBar> currentBossBars = new HashMap<>();

    /**
     * Load configuration section.
     */
    public void loadProgressionMessage() {
        isEnabled = configurationFiles.getConfigFile().getBoolean("progression_message.enabled");
        message = configurationFiles.getConfigFile().getString("progression_message.message");

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
                    .replace("%progression%", String.valueOf(progression))
                    .replace("%required%", String.valueOf(required)));

            switch (progressionMessageType) {
                case ACTIONBAR -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(toSend));
                case CHAT -> player.sendMessage(toSend);
                case BOSSBAR -> {
                    if (currentBossBars.containsKey(player)) {
                        currentBossBars.get(player).setTitle(toSend);
                        return;
                    }

                    final BossBar bossBar = Bukkit.getServer().createBossBar(toSend, BarColor.BLUE, BarStyle.SOLID);
                    bossBar.addPlayer(player);
                    currentBossBars.put(player, bossBar);

                    Bukkit.getScheduler().runTaskLater(ODailyQuests.INSTANCE, () -> removeBossBar(player), 100L);
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
