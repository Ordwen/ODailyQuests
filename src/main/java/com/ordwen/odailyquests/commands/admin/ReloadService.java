package com.ordwen.odailyquests.commands.admin;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.QuestSystem;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.configuration.integrations.OraxenEnabled;
import com.ordwen.odailyquests.externs.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.quests.categories.CategoriesLoader;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class ReloadService {

    private final ODailyQuests oDailyQuests;
    private final CategoriesLoader categoriesLoader;
    private final SQLManager sqlManager;
    private final YamlManager yamlManager;

    /**
     * Constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public ReloadService(ODailyQuests oDailyQuests, boolean useSQL) {
        this.oDailyQuests = oDailyQuests;
        this.categoriesLoader = oDailyQuests.getCategoriesLoader();

        if (useSQL) {
            this.sqlManager = oDailyQuests.getSQLManager();
            this.yamlManager = null;
        } else {
            this.yamlManager = oDailyQuests.getYamlManager();
            this.sqlManager = null;
        }
    }

    /**
     * Load all quests from connected players, to avoid errors on reload.
     */
    public void loadConnectedPlayerQuests(QuestSystem questSystem) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!questSystem.getActiveQuests().containsKey(player.getName())) {
                loadQuestsForPlayer(player, questSystem);
            }
        }
    }

    /**
     * Load quests for a specific player.
     *
     * @param player player to load quests for.
     */
    private void loadQuestsForPlayer(Player player, QuestSystem questSystem) {
        switch (Modes.getStorageMode()) {
            case "YAML" -> {
                if (yamlManager == null) restartNeeded();
                else {
                    yamlManager.getLoadProgressionYAML().loadPlayerQuests(questSystem, player.getName(), questSystem.getActiveQuests());
                }
            }
            case "MySQL", "H2" -> {
                if (sqlManager == null) restartNeeded();
                else {
                    sqlManager.getLoadProgressionSQL().loadProgression(questSystem, player.getName(), questSystem.getActiveQuests());
                }
            }
            default ->
                    PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
        }
    }

    /**
     * Save all quests from connected players, to avoid errors on reload.
     */
    public void saveConnectedPlayerQuests(boolean isAsync, QuestSystem questSystem) {

        final Set<String> playersToRemove = new HashSet<>();

        switch (Modes.getStorageMode()) {
            case "YAML" -> {
                if (yamlManager == null) restartNeeded();
                else {
                    for (String player : questSystem.getActiveQuests().keySet()) {
                        yamlManager.getSaveProgressionYAML().saveProgression(questSystem, player, questSystem.getActiveQuests().get(player), isAsync);
                        playersToRemove.add(player);
                    }
                }
            }
            case "MySQL", "H2" -> {
                if (sqlManager == null) restartNeeded();
                else {
                    for (String player : questSystem.getActiveQuests().keySet()) {
                        sqlManager.getSaveProgressionSQL().saveProgression(questSystem, player, questSystem.getActiveQuests().get(player), isAsync);
                        playersToRemove.add(player);
                    }
                }
            }
            default ->
                    PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
        }


        for (String player : playersToRemove) {
            questSystem.getActiveQuests().remove(player);
        }
    }

    /**
     * Execute all required actions when the command /qadmin reload is performed.
     */
    public void reload() {
        oDailyQuests.getFilesManager().loadAllFiles();

        /* Load holograms */
        HologramsManager.loadHolograms();

        /* Load specific settings */
        oDailyQuests.getConfigurationManager().loadConfiguration();

        /* Load quests & interface */
        if ((!ItemsAdderEnabled.isEnabled() || ItemsAdderEnabled.isLoaded())
                && (!OraxenEnabled.isEnabled() || OraxenEnabled.isLoaded())) {
            categoriesLoader.loadCategories();
            oDailyQuests.getInterfacesManager().initAllObjects();
        }

        ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
            saveConnectedPlayerQuests(true, questSystem);
            Bukkit.getScheduler().runTaskLater(oDailyQuests, new Runnable() {
                @Override
                public void run() {
                    loadConnectedPlayerQuests(questSystem);
                }
            }, 20L);
        });
    }

    private void restartNeeded() {
        PluginLogger.warn("-----------------------------------------------");
        PluginLogger.warn("It seems like you have changed the storage mode.");
        PluginLogger.warn("Please restart the server to apply the changes.");
        PluginLogger.warn("-----------------------------------------------");
    }
}
