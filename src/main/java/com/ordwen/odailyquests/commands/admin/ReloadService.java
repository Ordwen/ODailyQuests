package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.ConfigFactory;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.OraxenEnabled;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ReloadService {

    private final ODailyQuests plugin;
    private final CategoriesLoader categoriesLoader;

    /**
     * Constructor.
     *
     * @param plugin main class instance.
     */
    public ReloadService(ODailyQuests plugin) {
        this.plugin = plugin;
        this.categoriesLoader = plugin.getCategoriesLoader();
    }

    /**
     * Load all quests from connected players, to avoid errors on reload.
     */
    public void loadConnectedPlayerQuests() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
                plugin.getDatabaseManager().loadQuestsForPlayer(player.getName());
            }
        }
    }

    /**
     * Save all quests from connected players, to avoid errors on reload.
     */
    public void saveConnectedPlayerQuests() {
        final Map<String, PlayerQuests> activeQuests = new HashMap<>(QuestsManager.getActiveQuests());
        for (Map.Entry<String, PlayerQuests> entry : activeQuests.entrySet()) {
            plugin.getDatabaseManager().saveProgressionForPlayer(entry.getKey(), entry.getValue());
            QuestsManager.getActiveQuests().remove(entry.getKey());
        }
    }

    /**
     * Execute all required actions when the command /qadmin reload is performed.
     */
    public void reload() {
        /* load files */
        plugin.getFilesManager().loadAllFiles();

        System.out.println("TEST 1");
        /* load configurations */
        ConfigFactory.registerConfigs(plugin.getConfigurationFiles());

        System.out.println("TEST 2");
        /* load database */
        plugin.getDatabaseManager().load();

        System.out.println("TEST 3");
        /* load quests & interface */
        if ((!ItemsAdderEnabled.isEnabled() || ItemsAdderEnabled.isLoaded())
                && (!OraxenEnabled.isEnabled() || OraxenEnabled.isLoaded())) {
            categoriesLoader.loadCategories();
            plugin.getInterfacesManager().initAllObjects();
        }

        System.out.println("TEST 4");
        saveConnectedPlayerQuests();
        ODailyQuests.morePaperLib.scheduling().globalRegionalScheduler().runDelayed(this::loadConnectedPlayerQuests, 20L);
    }
}
