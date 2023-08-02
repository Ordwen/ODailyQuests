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

import java.util.ArrayList;

public class CategoriesLoader {

    private static final Category globalCategory = new Category("globalQuests");
    private static final Category easyCategory = new Category("easyQuests");
    private static final Category mediumCategory = new Category("mediumQuests");
    private static final Category hardCategory = new Category("hardQuests");

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
        FileConfiguration globalQuestsFile = QuestsFiles.getGlobalQuestsConfiguration();
        FileConfiguration easyQuestsFile = QuestsFiles.getEasyQuestsConfiguration();
        FileConfiguration mediumQuestsFile = QuestsFiles.getMediumQuestsConfiguration();
        FileConfiguration hardQuestsFile = QuestsFiles.getHardQuestsConfiguration();

        if (Modes.getQuestsMode() == 1) {

            /* load global quests */
            questsLoader.loadQuests(globalQuestsFile, globalCategory, "globalQuests");
            if (globalCategory.size() < QuestsAmount.getQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getQuestsAmount() + " quests in your globalQuest.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }

        } else if (Modes.getQuestsMode() == 2) {

            /* load easy quests */
            questsLoader.loadQuests(easyQuestsFile, easyCategory, "easyQuests");
            if (easyCategory.size() < QuestsAmount.getEasyQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getEasyQuestsAmount() + " quest in your easyQuests.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }

            /* load medium quests */
            questsLoader.loadQuests(mediumQuestsFile, mediumCategory, "mediumQuests");
            if (mediumCategory.size() < QuestsAmount.getMediumQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getMediumQuestsAmount() + " quest in your mediumQuests.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }

            /* load hard quests */
            questsLoader.loadQuests(hardQuestsFile, hardCategory, "hardQuests");
            if (hardCategory.size() < QuestsAmount.getHardQuestsAmount()) {
                PluginLogger.error("Impossible to enable the plugin.");
                PluginLogger.error("You need to have at least " + QuestsAmount.getHardQuestsAmount() + " quest in your hardQuests.yml file.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            }
        } else {
            PluginLogger.error("Impossible to load the quests. The selected mode is incorrect.");
        }
    }

    /**
     * Get category by name.
     * @param name category name.
     * @return category.
     */
    public static Category getCategoryByName(String name) {
        switch (name) {
            case "globalQuests" -> {
                return globalCategory;
            }
            case "easyQuests" -> {
                return easyCategory;
            }
            case "mediumQuests" -> {
                return mediumCategory;
            }
            case "hardQuests" -> {
                return hardCategory;
            }
        }

        return null;
    }

    /**
     * Get global quests.
     */
    public static ArrayList<AbstractQuest> getGlobalQuests() {
        return globalCategory;
    }

    /**
     * Get easy quests.
     */
    public static ArrayList<AbstractQuest> getEasyQuests() {
        return easyCategory;
    }

    /**
     * Get medium quests.
     */
    public static ArrayList<AbstractQuest> getMediumQuests() {
        return mediumCategory;
    }

    /**
     * Get hard quests.
     */
    public static ArrayList<AbstractQuest> getHardQuests() {
        return hardCategory;
    }
}
