package com.ordwen.odailyquests.commands;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.quests.LoadQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.storage.mysql.MySQLManager;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.ordwen.odailyquests.tools.TimeRemain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReloadService {

    private final ODailyQuests oDailyQuests;
    private final ConfigurationFiles configurationFiles;
    private final MySQLManager mySqlManager;
    private final YamlManager yamlManager;

    /**
     * Constructor.
     *
     * @param oDailyQuests main class instance.
     */
    public ReloadService(ODailyQuests oDailyQuests, boolean useMySQL) {
        this.oDailyQuests = oDailyQuests;
        this.configurationFiles = oDailyQuests.getConfigurationFiles();

        if (useMySQL) {
            this.mySqlManager = oDailyQuests.getMySqlManager();
            this.yamlManager = null;
        } else {
            this.yamlManager = oDailyQuests.getYamlManager();
            this.mySqlManager = null;
        }
    }

    /**
     * Load all quests from connected players, to avoid errors on reload.
     */
    public void loadConnectedPlayerQuests() {
        switch (configurationFiles.getConfigFile().getString("storage_mode")) {
            case "YAML":
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
                        yamlManager.getLoadProgressionYAML().loadPlayerQuests(player.getName(), QuestsManager.getActiveQuests(),
                                configurationFiles.getConfigFile().getInt("quests_mode"),
                                configurationFiles.getConfigFile().getInt("timestamp_mode"),
                                configurationFiles.getConfigFile().getInt("temporality_mode"));
                    }
                }
                break;
            case "MySQL":
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    if (!QuestsManager.getActiveQuests().containsKey(player.getName())) {
                        mySqlManager.getLoadProgressionSQL().loadProgression(player.getName(), QuestsManager.getActiveQuests(),
                                configurationFiles.getConfigFile().getInt("quests_mode"),
                                configurationFiles.getConfigFile().getInt("timestamp_mode"),
                                configurationFiles.getConfigFile().getInt("temporality_mode"));
                    }
                }
                break;
            default:
                PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
                break;
        }
    }

    /**
     * Save all quests from connected players, to avoid errors on reload.
     */
    public void saveConnectedPlayerQuests(boolean isAsync) {
        switch (configurationFiles.getConfigFile().getString("storage_mode")) {
            case "YAML":
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    yamlManager.getSaveProgressionYAML().saveProgression(player.getName(), QuestsManager.getActiveQuests().get(player.getName()));
                    QuestsManager.getActiveQuests().remove(player.getName());
                }
                break;
            case "MySQL":
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    mySqlManager.getSaveProgressionSQL().saveProgression(player.getName(), QuestsManager.getActiveQuests().get(player.getName()), isAsync);
                    QuestsManager.getActiveQuests().remove(player.getName());
                }
                break;
            default:
                PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
                break;
        }
    }

    /**
     * Execute all required actions when the command /qadmin reload is performed.
     */
    public void reload() {
        oDailyQuests.getFilesManager().loadAllFiles();
        oDailyQuests.getConfigurationManager().loadConfiguration();
        oDailyQuests.getInterfacesManager().initAllObjects();
        LoadQuests.loadCategories();

        saveConnectedPlayerQuests(true);
        loadConnectedPlayerQuests();
    }
}
