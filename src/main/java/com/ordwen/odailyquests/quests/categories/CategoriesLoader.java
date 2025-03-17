package com.ordwen.odailyquests.quests.categories;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.QuestsAmount;
import com.ordwen.odailyquests.files.QuestsFiles;
import com.ordwen.odailyquests.quests.QuestsLoader;
import com.ordwen.odailyquests.quests.types.AbstractQuest;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class CategoriesLoader {

    private static final String GLOBAL_QUESTS = "globalQuests";
    private static final String EASY_QUESTS = "easyQuests";
    private static final String MEDIUM_QUESTS = "mediumQuests";
    private static final String HARD_QUESTS = "hardQuests";

    private static final Category globalCategory = new Category(GLOBAL_QUESTS);
    private static final Category easyCategory = new Category(EASY_QUESTS);
    private static final Category mediumCategory = new Category(MEDIUM_QUESTS);
    private static final Category hardCategory = new Category(HARD_QUESTS);

    private final QuestsLoader questsLoader = new QuestsLoader();

    /**
     * Load all quests from files.
     */
    public void loadCategories() {

        globalCategory.clear();
        easyCategory.clear();
        mediumCategory.clear();
        hardCategory.clear();

        /* init files */
        final FileConfiguration globalQuestsFile = QuestsFiles.getGlobalQuestsConfiguration();
        final FileConfiguration easyQuestsFile = QuestsFiles.getEasyQuestsConfiguration();
        final FileConfiguration mediumQuestsFile = QuestsFiles.getMediumQuestsConfiguration();
        final FileConfiguration hardQuestsFile = QuestsFiles.getHardQuestsConfiguration();

        if (Modes.getQuestsMode() == 1) {

            /* load global quests */
            questsLoader.loadQuests(globalQuestsFile, globalCategory, GLOBAL_QUESTS);
            if (!validateCategory(globalCategory, QuestsAmount.getQuestsAmount(), GLOBAL_QUESTS)) {
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }
        } else if (Modes.getQuestsMode() == 2) {
            questsLoader.loadQuests(easyQuestsFile, easyCategory, EASY_QUESTS);
            questsLoader.loadQuests(mediumQuestsFile, mediumCategory, MEDIUM_QUESTS);
            questsLoader.loadQuests(hardQuestsFile, hardCategory, HARD_QUESTS);

            if (!validateCategory(easyCategory, QuestsAmount.getEasyQuestsAmount(), EASY_QUESTS) ||
                    !validateCategory(mediumCategory, QuestsAmount.getMediumQuestsAmount(), MEDIUM_QUESTS) ||
                    !validateCategory(hardCategory, QuestsAmount.getHardQuestsAmount(), HARD_QUESTS)) {
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }
        } else {
            PluginLogger.error("Impossible to load the quests. The selected mode is incorrect.");
            Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
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
        switch (name) {
            case GLOBAL_QUESTS -> {
                return globalCategory;
            }
            case EASY_QUESTS -> {
                return easyCategory;
            }
            case MEDIUM_QUESTS -> {
                return mediumCategory;
            }
            case HARD_QUESTS -> {
                return hardCategory;
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * Get global quests.
     */
    public static List<AbstractQuest> getGlobalQuests() {
        return globalCategory;
    }

    /**
     * Get easy quests.
     */
    public static List<AbstractQuest> getEasyQuests() {
        return easyCategory;
    }

    /**
     * Get medium quests.
     */
    public static List<AbstractQuest> getMediumQuests() {
        return mediumCategory;
    }

    /**
     * Get hard quests.
     */
    public static List<AbstractQuest> getHardQuests() {
        return hardCategory;
    }
}
