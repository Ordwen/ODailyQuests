package com.ordwen.odailyquests.quests.categories;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.configuration.essentials.SafetyMode;
import com.ordwen.odailyquests.files.implementations.QuestsFiles;
import com.ordwen.odailyquests.quests.QuestsLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class CategoriesLoader {

    private static final Map<String, Category> categories = new LinkedHashMap<>();

    private final QuestsLoader questsLoader = new QuestsLoader();

    /**
     * Load all quests from files.
     */
    public void loadCategories() {
        categories.clear();

        final boolean safetyMode = SafetyMode.isSafetyModeEnabled();

        for (Map.Entry<String, Integer> entry : QuestsPerCategory.getAllAmounts().entrySet()) {
            final String categoryName = entry.getKey();
            final int requiredAmount = entry.getValue();

            final Category category = new Category(categoryName);
            categories.put(categoryName, category);

            final FileConfiguration configFile = QuestsFiles.getQuestsConfigurationByCategory(categoryName);
            if (configFile == null) {
                PluginLogger.error("Failed to load configuration file for " + categoryName + ". Plugin will be disabled.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                return;
            }

            questsLoader.loadQuests(configFile, category, categoryName);
            if (!validateCategory(category, requiredAmount, categoryName, safetyMode)) {
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                return;
            }
        }
    }

    /**
     * Validate that a category has enough quests and enough quests without permission.
     *
     * @param category       The quest category.
     * @param requiredAmount The required number of quests.
     * @param categoryName   The name of the category (for logs).
     * @return true if valid, false otherwise.
     */
    private boolean validateCategory(Category category, int requiredAmount, String categoryName, boolean safetyMode) {
        final int totalQuests = category.size();
        final int publicQuests = (int) category
                .stream()
                .map(AbstractQuest::getRequiredPermissions)
                .map(perms -> perms == null || perms.isEmpty())
                .filter(Boolean::booleanValue)
                .count();

        if (totalQuests < requiredAmount) {
            PluginLogger.error("Impossible to enable the plugin.");
            PluginLogger.error("You need at least " + requiredAmount + " quests in your " + categoryName + ".yml file.");
            return false;
        }

        if (safetyMode) {
            if (publicQuests < requiredAmount) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("Category '" + categoryName + "': only " + publicQuests + " public quest(s) but " + requiredAmount + " required (safety_mode=true).");
                PluginLogger.error("Disable 'safety_mode' if you want permission-gated categories; " + "note players without permissions may end up with no quests.");
                return false;
            }
        } else if (publicQuests == 0) {
            PluginLogger.warn("Category '" + categoryName + "' has no public quests. " + "Players without permissions may receive 0 quests (safety_mode=false).");
        }

        return true;
    }

    /**
     * Get category by name.
     *
     * @param name category name.
     * @return category.
     */
    public static Category getCategoryByName(String name) {
        return categories.get(name);
    }

    /**
     * Get all categories.
     *
     * @return all categories.
     */
    public static Map<String, Category> getAllCategories() {
        return categories;
    }

    public static boolean hasCategory(String categoryName) {
        return categories.containsKey(categoryName);
    }
}
