package com.ordwen.odailyquests.quests.categories;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.QuestsPerCategory;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.quests.QuestsLoader;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class CategoriesLoader {

    private static final Map<String, Category> categories = new HashMap<>();

    private final QuestsLoader questsLoader = new QuestsLoader();

    /**
     * Load all quests from files.
     */
    public void loadCategories() {
        categories.clear();

        for (Map.Entry<String, Integer> entry : QuestsPerCategory.getAllAmounts().entrySet()) {
            final String categoryName = entry.getKey();
            int requiredAmount = entry.getValue();

            final Category category = new Category(categoryName);
            categories.put(categoryName, category);

            final FileConfiguration configFile = QuestsFiles.getQuestsConfigurationByCategory(categoryName);
            if (configFile != null) {
                questsLoader.loadQuests(configFile, category, categoryName);
                if (!validateCategory(category, requiredAmount, categoryName)) {
                    Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                    return;
                }
            } else {
                PluginLogger.error("Failed to load configuration file for " + categoryName + ". Plugin will be disabled.");
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
    private boolean validateCategory(Category category, int requiredAmount, String categoryName) {
        int totalQuests = category.size();
        int noPermQuests = (int) category.stream().filter(q -> q.getRequiredPermission() == null).count();

        if (totalQuests < requiredAmount || noPermQuests < requiredAmount) {
            PluginLogger.error("Impossible to enable the plugin.");
            PluginLogger.error("You need at least " + requiredAmount + " quests in your " + categoryName + ".yml file.");
            PluginLogger.error("Also, at least " + requiredAmount + " quests must be accessible without permission.");
            return false;
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
