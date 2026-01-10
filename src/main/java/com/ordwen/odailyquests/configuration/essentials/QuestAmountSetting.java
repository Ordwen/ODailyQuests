package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.TextFormatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Represents the amount of quests assigned for a category.
 * <p>
 * The amount can either be a fixed integer defined in the configuration or a
 * PlaceholderAPI expression evaluated per player.
 */
public final class QuestAmountSetting {

    private final String categoryName;
    private final String rawValue;
    private final Integer staticAmount;

    private QuestAmountSetting(String categoryName, String rawValue, Integer staticAmount) {
        this.categoryName = categoryName;
        this.rawValue = rawValue;
        this.staticAmount = staticAmount;
    }

    /**
     * Creates a {@link QuestAmountSetting} from the raw configuration value.
     *
     * @param categoryName the related category
     * @param rawValue     the raw configuration value
     * @return a parsed {@link QuestAmountSetting}
     */
    public static QuestAmountSetting from(String categoryName, Object rawValue) {
        Objects.requireNonNull(rawValue, "rawValue");
        final String trimmed = rawValue.toString().trim();

        if (trimmed.isEmpty()) {
            PluginLogger.error("Invalid quests_per_category entry for '" + categoryName + "': value is empty.");
            throw new IllegalArgumentException("Quest amount cannot be empty");
        }

        if (trimmed.matches("^-?\\d+$")) {
            final int amount = Integer.parseInt(trimmed);
            if (amount <= 0) {
                PluginLogger.error("Invalid quests_per_category entry for '" + categoryName + "': value must be > 0.");
                throw new IllegalArgumentException("Quest amount must be positive");
            }
            return new QuestAmountSetting(categoryName, trimmed, amount);
        }

        return new QuestAmountSetting(categoryName, trimmed, null);
    }

    /**
     * Resolves the configured value for a player.
     * <p>
     * If the value is static, it is returned immediately. Otherwise, the
     * placeholder expression is evaluated for the provided player.
     *
     * @param player the player context
     * @return the resolved amount (never negative)
     */
    public int resolve(Player player) {
        if (!isDynamic()) {
            return staticAmount;
        }

        if (player == null) {
            PluginLogger.warn("Cannot resolve quests amount for category '" + categoryName + "' without player context.");
            return 0;
        }

        if (!TextFormatter.isPlaceholderAPIEnabled()) {
            PluginLogger.warn("Category '" + categoryName + "' uses PlaceholderAPI but it is not enabled.");
            return 0;
        }

        final String formatted = ChatColor.stripColor(TextFormatter.format(player, rawValue));
        if (formatted == null || formatted.trim().isEmpty()) {
            PluginLogger.warn("Placeholder for category '" + categoryName + "' returned an empty value. Defaulting to 0 quest.");
            return 0;
        }

        try {
            final double value = Double.parseDouble(formatted.trim());
            return Math.max((int) Math.floor(value), 0);
        } catch (NumberFormatException exception) {
            PluginLogger.warn("Unable to parse quests amount for category '" + categoryName + "' (value='" + formatted + "').");
            return 0;
        }
    }

    /**
     * Indicates whether the amount relies on PlaceholderAPI.
     *
     * @return {@code true} if the value is dynamic
     */
    public boolean isDynamic() {
        return staticAmount == null;
    }

    /**
     * Returns the static amount, if defined.
     *
     * @return the configured static amount or {@code null}
     */
    public Integer getStaticAmount() {
        return staticAmount;
    }
}