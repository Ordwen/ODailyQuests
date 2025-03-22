package com.ordwen.odailyquests.configuration.integrations;

import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.tools.TextFormatter;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class NPCNames implements IConfigurable {

    private final ConfigurationFile configurationFile;

    public NPCNames(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    private String playerNPCName;
    private final Map<String, String> categoryNPCNames = new HashMap<>();

    @Override
    public void load() {
        final ConfigurationSection section = configurationFile.getConfig().getConfigurationSection("npcs");

        if (section == null) {
            PluginLogger.error("NPCs names section not found in the config. NPCs names will not be loaded.");
            return;
        }

        playerNPCName = TextFormatter.format(section.getString(".player"));

        categoryNPCNames.clear();
        for (String categoryName : CategoriesLoader.getAllCategories().keySet()) {
            categoryNPCNames.put(TextFormatter.format(section.getString(categoryName)), categoryName);
        }
    }

    private static NPCNames getInstance() {
        return ConfigFactory.getConfig(NPCNames.class);
    }

    public static String getPlayerNPCName() {
        return getInstance().playerNPCName;
    }

    public static String getCategoryByNPCName(String npcName) {
        return getInstance().categoryNPCNames.get(npcName);
    }

    public static boolean isCategoryForNPCName(String npcName) {
        return getInstance().categoryNPCNames.containsKey(npcName);
    }
}
