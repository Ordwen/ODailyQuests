package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Modes;
import com.ordwen.odailyquests.configuration.essentials.Temporality;
import com.ordwen.odailyquests.configuration.integrations.ItemsAdderEnabled;
import com.ordwen.odailyquests.externs.hooks.holograms.HologramsManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.quests.QuestsLoader;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.tools.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReloadService {

    private final ODailyQuests oDailyQuests;
    private final QuestsLoader questsLoader;
    private final SQLManager sqlManager;
    private final YamlManager yamlManager;

    /**
     * Constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public ReloadService(ODailyQuests oDailyQuests, boolean useSQL) {
        this.oDailyQuests = oDailyQuests;
        this.questsLoader = oDailyQuests.getQuestsLoader();

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
    public void loadConnectedPlayerQuests() {
        switch (Modes.getStorageMode()) {
            case "YAML" -> {
                if (yamlManager == null) restartNeeded();
                else {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
                            yamlManager.getLoadProgressionYAML().loadPlayerQuests(player.getName(), QuestsManager.getActiveQuests(),
                                    Modes.getQuestsMode(),
                                    Modes.getTimestampMode(),
                                    Temporality.getTemporalityMode());
                        }
                    }
                }
            }
            case "MySQL", "H2" -> {
                if (sqlManager == null) restartNeeded();
                else {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
                            sqlManager.getLoadProgressionSQL().loadProgression(player.getName(), QuestsManager.getActiveQuests(),
                                    Modes.getQuestsMode(),
                                    Modes.getTimestampMode(),
                                    Temporality.getTemporalityMode());
                        }
                    }
                }
            }
            default ->
                    PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
        }
    }

    /**
     * Save all quests from connected players, to avoid errors on reload.
     */
    public void saveConnectedPlayerQuests(boolean isAsync) {
        switch (Modes.getStorageMode()) {
            case "YAML" -> {
                if (yamlManager == null) restartNeeded();
                else {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        yamlManager.getSaveProgressionYAML().saveProgression(player.getName(), QuestsManager.getActiveQuests().get(player.getName()), isAsync);
                        QuestsManager.getActiveQuests().remove(player.getName());
                    }
                }
            }
            case "MySQL", "H2" -> {
                if (sqlManager == null) restartNeeded();
                else {
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        sqlManager.getSaveProgressionSQL().saveProgression(player.getName(), QuestsManager.getActiveQuests().get(player.getName()), isAsync);
                        QuestsManager.getActiveQuests().remove(player.getName());
                    }
                }
            }
            default ->
                    PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
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
        if (!ItemsAdderEnabled.isEnabled() || ItemsAdderEnabled.isLoaded()) {
            questsLoader.loadCategories();
            oDailyQuests.getInterfacesManager().initAllObjects();
        }

        saveConnectedPlayerQuests(true);
        Bukkit.getScheduler().runTaskLater(oDailyQuests, this::loadConnectedPlayerQuests, 20L);
    }

    private void restartNeeded() {
        PluginLogger.warn("-----------------------------------------------");
        PluginLogger.warn("It seems like you have changed the storage mode.");
        PluginLogger.warn("Please restart the server to apply the changes.");
        PluginLogger.warn("-----------------------------------------------");
    }
}
