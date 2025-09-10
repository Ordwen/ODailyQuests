package com.ordwen.odailyquests.configuration.functionalities.progression;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.nms.NMSHandler;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Displays a native Minecraft toast using a temporary advancement.
 * <p>
 * Config section: {@code toast}
 * <ul>
 *     <li>{@code enabled} (boolean)</li>
 *     <li>{@code frame} (TASK|GOAL|CHALLENGE)</li>
 *     <li>{@code icon} (Material)</li>
 *     <li>{@code icon_custom_model_data} (int, optional)</li>
 *     <li>{@code text} (string, supports %player% and %questName%) — the line shown on the toast</li>
 * </ul>
 */
public class ToastNotification implements IConfigurable {

    private static final String CONFIG_ROOT = "toast";
    private static final int CLEANUP_TICKS = 40;

    private final ConfigurationFile configurationFile;

    private boolean enabled;
    private Frame frame;
    private Material icon;
    private Integer customModelData;
    private String text;

    private enum IconFormat {LEGACY_TAG, MODERN_INT, MODERN_FLOAT}

    private enum Frame {
        TASK("task"), GOAL("goal"), CHALLENGE("challenge");
        private final String json;

        Frame(String json) {
            this.json = json;
        }

        public String json() {
            return json;
        }

        static Frame parse(String value) {
            if (value == null) return Frame.TASK;
            try {
                return Frame.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                PluginLogger.warn("Invalid frame type '" + value + "', defaulting to TASK.");
                return Frame.TASK;
            }
        }
    }

    public ToastNotification(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection(CONFIG_ROOT);

        if (section == null) {
            PluginLogger.warn("Toast section is missing in the configuration file. Disabling.");
            enabled = false;
            return;
        }

        enabled = section.getBoolean("enabled", false);
        if (!enabled) return;

        frame = Frame.parse(section.getString("frame", "TASK"));

        final String iconName = section.getString("icon", "PAPER");
        icon = parseMaterial(iconName);

        customModelData = section.isInt("custom_model_data") ? section.getInt("custom_model_data") : null;

        text = TextFormatter.format(section.getString("text", "&aQuest completed!"));
    }

    /**
     * Sends a toast to a player if enabled.
     *
     * @param player    target player
     * @param questName quest display name
     */
    public void sendToastNotificationInternal(Player player, String questName) {
        if (!enabled || player == null) return;

        final String visibleLine = formatForPlayer(text, player, questName);
        final NamespacedKey key = new NamespacedKey(ODailyQuests.INSTANCE, uniqueKey(player));
        final String json = buildAdvancementJson(visibleLine);

        new BukkitRunnable() {
            @Override
            public void run() {
                attemptToast(player, key, json);
            }
        }.runTask(ODailyQuests.INSTANCE);
    }

    private void attemptToast(Player player, NamespacedKey key, String json) {
        try {
            if (!loadAdvancement(key, json)) return;
            awardAllCriteria(player, key);
            scheduleCleanup(player, key);
        } catch (Exception e) {
            PluginLogger.error("Unable to send toast: " + e.getMessage());
            safeRemoveAdvancement(key);
        }
    }

    @SuppressWarnings("deprecation")
    private boolean loadAdvancement(NamespacedKey key, String json) {
        Bukkit.getUnsafe().loadAdvancement(key, json);
        final Advancement adv = Bukkit.getAdvancement(key);
        if (adv == null) {
            PluginLogger.error("Failed to load advancement for key: " + key);
            return false;
        }
        return true;
    }

    private void awardAllCriteria(Player player, NamespacedKey key) {
        final Advancement adv = Bukkit.getAdvancement(key);
        if (adv == null) return;

        final AdvancementProgress progress = player.getAdvancementProgress(adv);
        for (String c : adv.getCriteria()) {
            if (!progress.isDone()) progress.awardCriteria(c);
        }
    }

    private void scheduleCleanup(Player player, NamespacedKey key) {
        new BukkitRunnable() {
            @Override
            public void run() {
                revokeAndRemove(player, key);
            }
        }.runTaskLater(ODailyQuests.INSTANCE, CLEANUP_TICKS);
    }

    private void revokeAndRemove(Player player, NamespacedKey key) {
        try {
            final Advancement adv = Bukkit.getAdvancement(key);
            if (adv != null) {
                final AdvancementProgress p = player.getAdvancementProgress(adv);
                for (String c : adv.getCriteria()) {
                    if (p.getAwardedCriteria().contains(c)) p.revokeCriteria(c);
                }
            }
        } catch (Exception e) {
            PluginLogger.error("Unable to revoke advancement criteria: " + e.getMessage());
        } finally {
            safeRemoveAdvancement(key);
        }
    }

    @SuppressWarnings("deprecation")
    private void safeRemoveAdvancement(NamespacedKey key) {
        try {
            Bukkit.getUnsafe().removeAdvancement(key);
        } catch (Exception e) {
            PluginLogger.error("Unable to remove advancement: " + e.getMessage());
        }
    }

    private static ToastNotification getInstance() {
        return ConfigFactory.getConfig(ToastNotification.class);
    }

    public static void sendToastNotification(Player player, String questName) {
        getInstance().sendToastNotificationInternal(player, questName);
    }

    private String uniqueKey(Player player) {
        return "toast_" + player.getUniqueId() + "_" + Long.toHexString(ThreadLocalRandom.current().nextLong());
    }

    private String buildAdvancementJson(String toastVisibleLine) {
        final String iconKey = namespacedKeyOf(icon);
        final String iconJson = buildIconJson(iconKey, customModelData);

        final String display = "{" +
                "\"icon\":" + iconJson + "," +
                // toast visible line
                "\"title\":\"" + jsonEscape(toastVisibleLine) + "\"," +
                // required but invisible description
                "\"description\":\" \"," +
                "\"frame\":\"" + frame.json() + "\"," +
                "\"announce_to_chat\":false," +
                "\"show_toast\":true," +
                "\"hidden\":true" +
                "}";

        return "{"
                + "\"criteria\":{\"instant\":{\"trigger\":\"minecraft:impossible\"}},"
                + "\"display\":" + display
                + "}";
    }

    private String buildIconJson(String namespacedItemId, Integer cmd) {
        final IconFormat format = selectIconFormat(NMSHandler.getInstance().getVersion());

        if (format == IconFormat.LEGACY_TAG) {
            // 1.16 - 1.20.4 : item + nbt
            return (cmd == null)
                    ? "{\"item\":\"" + namespacedItemId + "\"}"
                    : "{\"item\":\"" + namespacedItemId + "\",\"nbt\":\"{CustomModelData:" + cmd + "}\"}";
        }

        // Formats modernes (id + components)
        if (cmd == null) {
            return "{\"id\":\"" + namespacedItemId + "\"}";
        }

        final String components = (format == IconFormat.MODERN_FLOAT)
                ? "{\"minecraft:custom_model_data\":{\"floats\":[" + cmd + "]}}" // 1.21.2+
                : "{\"minecraft:custom_model_data\":" + cmd + "}"; // 1.20.5 - 1.21.1

        return "{\"id\":\"" + namespacedItemId + "\",\"components\":" + components + "}";
    }

    private IconFormat selectIconFormat(String version) {
        // version ex: "1.21.8"
        final String[] p = version.split("\\.");
        final int major = parseIntSafe(p, 0);
        final int minor = parseIntSafe(p, 1);
        final int patch = parseIntSafe(p, 2);

        // 1.16 - 1.20.4
        if (major == 1 && (minor < 20 || (minor == 20 && patch <= 4))) return IconFormat.LEGACY_TAG;

        // 1.20.5 - 1.21.1
        if (major == 1 && ((minor == 20 /* && patch >= 5 */) || (minor == 21 && patch <= 1)))
            return IconFormat.MODERN_INT;

        // 1.21.2+
        return IconFormat.MODERN_FLOAT;
    }

    private int parseIntSafe(String[] parts, int idx) {
        if (idx >= parts.length) return 0;
        try {
            return Integer.parseInt(parts[idx]);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String namespacedKeyOf(Material mat) {
        try {
            return mat.getKey().toString();
        } catch (Exception exception) {
            PluginLogger.warn("Failed to get NamespacedKey for material " + mat + ", defaulting to minecraft:paper");
            return "minecraft:paper";
        }
    }

    private String jsonEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private Material parseMaterial(String name) {
        try {
            return Material.valueOf(name.trim().toUpperCase());
        } catch (Exception e) {
            PluginLogger.warn("Invalid material name '" + name + "', defaulting to PAPER.");
            return Material.PAPER;
        }
    }

    private String formatForPlayer(String template, Player player, String questName) {
        final String parsedQuest = TextFormatter.format(player, questName);
        final String raw = template
                .replace("%player%", player.getDisplayName())
                .replace("%questName%", parsedQuest);
        return TextFormatter.format(player, raw);
    }
}
