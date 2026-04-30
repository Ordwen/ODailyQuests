package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.implementations.ConfigurationFile;
import com.ordwen.odailyquests.quests.categories.CategoryGroup;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads and manages category groups from the configuration.
 * <p>
 * Category groups allow different categories to have different renewal schedules.
 * If no groups are defined, a default legacy group is created containing all categories.
 */
public class CategoryGroupsLoader implements IConfigurable {

    private static final List<DateTimeFormatter> TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("h:mma"),
            DateTimeFormatter.ofPattern("ha"),
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("H")
    );

    private static final String DEFAULT_TIME = "00:00";
    private static final String DEFAULT_ZONE = "SystemDefault";
    private static final String DEFAULT_INTERVAL = "1d";
    private static final String LEGACY_GROUP_NAME = "default";

    private final ConfigurationFile configurationFile;
    private final Map<String, CategoryGroup> groups = new LinkedHashMap<>();
    private final Map<String, String> categoryToGroup = new HashMap<>();
    private boolean legacyMode = false;

    public CategoryGroupsLoader(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        groups.clear();
        categoryToGroup.clear();

        final ConfigurationSection groupsSection = configurationFile.getConfig().getConfigurationSection("category_groups");

        if (groupsSection == null || groupsSection.getKeys(false).isEmpty()) {
            legacyMode = true;
            PluginLogger.info("No category_groups defined. Using legacy mode with global temporal settings.");
            createLegacyGroup();
            return;
        }

        legacyMode = false;
        loadGroups(groupsSection);
    }

    /**
     * Creates a legacy group containing all categories defined in quests_per_category,
     * using the global renew_time and renew_interval settings.
     */
    private void createLegacyGroup() {
        final ConfigurationSection questsSection = configurationFile.getConfig().getConfigurationSection("quests_per_category");
        if (questsSection == null) {
            PluginLogger.error("No quests_per_category section found. Cannot create legacy group.");
            return;
        }

        final List<String> allCategories = new ArrayList<>(questsSection.getKeys(false));
        final LocalTime renewTime = RenewTime.getRenewTime();
        final Duration renewInterval = RenewInterval.getRenewInterval();
        final ZoneId zoneId = RenewTime.getZoneId();

        final CategoryGroup legacyGroup = new CategoryGroup(
                LEGACY_GROUP_NAME,
                allCategories,
                renewTime,
                renewInterval,
                zoneId
        );

        groups.put(LEGACY_GROUP_NAME, legacyGroup);
        for (String category : allCategories) {
            categoryToGroup.put(category.toLowerCase(), LEGACY_GROUP_NAME);
        }

        PluginLogger.info("Created legacy group '" + LEGACY_GROUP_NAME + "' with " + allCategories.size() + " categories.");
    }

    /**
     * Loads category groups from the configuration section.
     */
    private void loadGroups(ConfigurationSection groupsSection) {
        final Set<String> assignedCategories = new HashSet<>();

        for (String groupName : groupsSection.getKeys(false)) {
            final ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);
            if (groupSection == null) {
                PluginLogger.warn("Invalid group configuration for '" + groupName + "'. Skipping.");
                continue;
            }

            final CategoryGroup group = parseGroup(groupName, groupSection, assignedCategories);
            if (group != null) {
                groups.put(groupName, group);
                for (String category : group.getCategoryNames()) {
                    categoryToGroup.put(category.toLowerCase(), groupName);
                }
                PluginLogger.info("Loaded category group '" + groupName + "' with categories: " + group.getCategoryNames()
                        + " (interval: " + formatDuration(group.getRenewInterval()) + ")");
            }
        }

        if (groups.isEmpty()) {
            PluginLogger.warn("No valid category groups loaded. Falling back to legacy mode.");
            legacyMode = true;
            createLegacyGroup();
        }
    }

    /**
     * Parses a single category group from its configuration section.
     */
    private CategoryGroup parseGroup(String groupName, ConfigurationSection section, Set<String> assignedCategories) {
        // Parse categories
        final List<String> categories = section.getStringList("categories");
        if (categories.isEmpty()) {
            PluginLogger.error("Group '" + groupName + "' has no categories defined. Skipping.");
            return null;
        }

        // Check for duplicate category assignments
        for (String category : categories) {
            final String lowerCategory = category.toLowerCase();
            if (assignedCategories.contains(lowerCategory)) {
                PluginLogger.error("Category '" + category + "' is already assigned to another group. Each category can only belong to one group.");
                return null;
            }
            assignedCategories.add(lowerCategory);
        }

        // Parse temporal settings with fallback to global settings
        final String timeStr = section.getString("renew_time", DEFAULT_TIME);
        final String intervalStr = section.getString("renew_interval", DEFAULT_INTERVAL);
        final String zoneStr = section.getString("renew_time_zone", DEFAULT_ZONE);

        final LocalTime renewTime = parseTime(timeStr, groupName);
        final Duration renewInterval = parseDuration(intervalStr, groupName);
        final ZoneId zoneId = parseZone(zoneStr, groupName);

        return new CategoryGroup(groupName, categories, renewTime, renewInterval, zoneId);
    }

    /**
     * Parses a time string with multiple format support.
     */
    private LocalTime parseTime(String timeStr, String groupName) {
        final String normalized = timeStr.toUpperCase().trim();
        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }
        PluginLogger.warn("Invalid time format '" + timeStr + "' for group '" + groupName + "'. Using default: " + DEFAULT_TIME);
        return LocalTime.MIDNIGHT;
    }

    /**
     * Parses a duration string (e.g., "1d", "7d", "12h", "1d12h").
     */
    private Duration parseDuration(String intervalStr, String groupName) {
        final Pattern pattern = Pattern.compile("(\\d+)([dhm])");
        final Matcher matcher = pattern.matcher(intervalStr.toLowerCase());
        Duration duration = Duration.ZERO;

        while (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2);

            duration = switch (unit) {
                case "d" -> duration.plus(value, ChronoUnit.DAYS);
                case "h" -> duration.plus(value, ChronoUnit.HOURS);
                case "m" -> duration.plus(value, ChronoUnit.MINUTES);
                default -> duration;
            };
        }

        if (duration.isZero()) {
            PluginLogger.warn("Invalid interval '" + intervalStr + "' for group '" + groupName + "'. Using default: " + DEFAULT_INTERVAL);
            return Duration.ofDays(1);
        }

        return duration;
    }

    /**
     * Parses a timezone string.
     */
    private ZoneId parseZone(String zoneStr, String groupName) {
        if (zoneStr.equalsIgnoreCase(DEFAULT_ZONE)) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(zoneStr);
        } catch (Exception e) {
            PluginLogger.warn("Invalid timezone '" + zoneStr + "' for group '" + groupName + "'. Using system default.");
            return ZoneId.systemDefault();
        }
    }

    /**
     * Formats a duration for logging purposes.
     */
    private String formatDuration(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        return sb.length() > 0 ? sb.toString() : "0m";
    }

    // Static accessor methods

    private static CategoryGroupsLoader getInstance() {
        return ConfigFactory.getConfig(CategoryGroupsLoader.class);
    }

    /**
     * Checks if the plugin is running in legacy mode (no category groups defined).
     *
     * @return true if in legacy mode
     */
    public static boolean isLegacyMode() {
        return getInstance().legacyMode;
    }

    /**
     * Gets all loaded category groups.
     *
     * @return an unmodifiable map of group name to CategoryGroup
     */
    public static Map<String, CategoryGroup> getAllGroups() {
        return Collections.unmodifiableMap(getInstance().groups);
    }

    /**
     * Gets a category group by name.
     *
     * @param groupName the name of the group
     * @return the CategoryGroup, or null if not found
     */
    public static CategoryGroup getGroup(String groupName) {
        return getInstance().groups.get(groupName);
    }

    /**
     * Gets the group that a category belongs to.
     *
     * @param categoryName the name of the category
     * @return the CategoryGroup, or null if the category is not assigned to any group
     */
    public static CategoryGroup getGroupForCategory(String categoryName) {
        final String groupName = getInstance().categoryToGroup.get(categoryName.toLowerCase());
        return groupName != null ? getInstance().groups.get(groupName) : null;
    }

    /**
     * Gets the name of the group that a category belongs to.
     *
     * @param categoryName the name of the category
     * @return the group name, or null if not found
     */
    public static String getGroupNameForCategory(String categoryName) {
        return getInstance().categoryToGroup.get(categoryName.toLowerCase());
    }

    /**
     * Gets all group names.
     *
     * @return a set of all group names
     */
    public static Set<String> getGroupNames() {
        return getInstance().groups.keySet();
    }
}
