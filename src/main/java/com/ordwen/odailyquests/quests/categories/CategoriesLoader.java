package com.ordwen.odailyquests.quests.categories;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.quests.QuestsLoader;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;

public class CategoriesLoader {

    /**
     * Load all quests from files.
     */
    public void loadCategories() {

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            questSystem.setQuestsLoader(new QuestsLoader());
            questSystem.setGlobalCategory(new Category("globalQuests"));
            questSystem.setEasyCategory(new Category("easyQuests"));
            questSystem.setMediumCategory(new Category("mediumQuests"));
            questSystem.setHardCategory(new Category("hardQuests"));
            questSystem.getGlobalCategory().clear();
            questSystem.getEasyCategory().clear();
            questSystem.getMediumCategory().clear();
            questSystem.getHardCategory().clear();

            QuestsLoader questsLoader = questSystem.getQuestsLoader();
            if (questSystem.getQuestsMode() == 1) {

                /* load global quests */
                questsLoader.loadQuests(questSystem.getGlobalQuestsConfig(), questSystem.getGlobalCategory(), "globalQuests");
                if (questSystem.getGlobalCategory().size() < questSystem.getGlobalQuestsAmount()) {
                    PluginLogger.error("Impossible to enable the plugin.");
                    PluginLogger.error("You need to have at least " + questSystem.getGlobalQuestsAmount() + " quests in your globalQuest.yml file.");
                    Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                }

            } else if (questSystem.getQuestsMode() == 2) {

                /* load easy quests */
                questsLoader.loadQuests(questSystem.getEasyQuestsConfig(), questSystem.getEasyCategory(), "easyQuests");
                if (questSystem.getEasyCategory().size() < questSystem.getEasyQuestsAmount()) {
                    PluginLogger.error("Impossible to enable the plugin.");
                    PluginLogger.error("You need to have at least " + questSystem.getEasyQuestsAmount() + " quest in your easyQuests.yml file.");
                    Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                }

                /* load medium quests */
                questsLoader.loadQuests(questSystem.getMediumQuestsConfig(), questSystem.getMediumCategory(), "mediumQuests");
                if (questSystem.getMediumCategory().size() < questSystem.getMediumQuestsAmount()) {
                    PluginLogger.error("Impossible to enable the plugin.");
                    PluginLogger.error("You need to have at least " + questSystem.getMediumQuestsAmount() + " quest in your mediumQuests.yml file.");
                    Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                }

                /* load hard quests */
                questsLoader.loadQuests(questSystem.getHardQuestsConfig(), questSystem.getHardCategory(), "hardQuests");
                if (questSystem.getHardCategory().size() < questSystem.getHardQuestsAmount()) {
                    PluginLogger.error("Impossible to enable the plugin.");
                    PluginLogger.error("You need to have at least " + questSystem.getHardQuestsAmount() + " quest in your hardQuests.yml file.");
                    Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                }
            } else {
                PluginLogger.error("Impossible to load the quests. The selected mode is incorrect.");
            }
        });
    }

    /**
     * Get category by name.
     * @param name category name.
     * @return category.
     */
    public static Category getCategoryByName(QuestSystem questSystem, String name) {
        switch (name) {
            case "globalQuests" -> {
                return questSystem.getGlobalCategory();
            }
            case "easyQuests" -> {
                return questSystem.getEasyCategory();
            }
            case "mediumQuests" -> {
                return questSystem.getMediumCategory();
            }
            case "hardQuests" -> {
                return questSystem.getHardCategory();
            }
        }

        return null;
    }
}
