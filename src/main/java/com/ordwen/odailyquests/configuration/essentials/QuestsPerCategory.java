package com.ordwen.odailyquests.configuration.essentials;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.IConfigurable;
import com.ordwen.odailyquests.files.ConfigurationFile;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class QuestsPerCategory implements IConfigurable {

    private final ConfigurationFile configurationFile;
    private final Map<String, Integer> questsAmounts = new LinkedHashMap<>();
    private int totalQuestsAmount;

    public QuestsPerCategory(ConfigurationFile configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Override
    public void load() {
        final FileConfiguration config = configurationFile.getConfig();

        totalQuestsAmount = 0;
        questsAmounts.clear();

        final ConfigurationSection section = config.getConfigurationSection("quests_per_category");
        if (section == null) {
            PluginLogger.error("No quests_per_category section found! Please check your configuration file.");
            PluginLogger.error("Impossible to load quests. Disabling plugin.");
            Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
            return;
        }

        for (String category : section.getKeys(false)) {
            int amount = section.getInt(category);
            if (amount <= 0) {
                PluginLogger.error("Invalid amount of quests for category " + category + ". Please check your configuration file.");
                PluginLogger.error("Impossible to load quests. Disabling plugin.");
                Bukkit.getPluginManager().disablePlugin(ODailyQuests.INSTANCE);
                return;
            }

            questsAmounts.put(category, amount);
            totalQuestsAmount += amount;
        }
    }

    public static int getAmountForCategory(String name) {
        return getInstance().questsAmounts.getOrDefault(name, -1);
    }

    private static QuestsPerCategory getInstance() {
        return ConfigFactory.getConfig(QuestsPerCategory.class);
    }

    public static int getTotalQuestsAmount() {
        return getInstance().totalQuestsAmount;
    }

    public static Map<String, Integer> getAllAmounts() {
        return getInstance().questsAmounts;
    }
}
