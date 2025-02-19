package com.ordwen.odailyquests.quests.player.progression.storage;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.h2.H2Manager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql.MySQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.tools.PluginLogger;

import java.util.Map;

public class DatabaseManager {

    private final ODailyQuests plugin;

    private SQLManager sqlManager;
    private YamlManager yamlManager;

    public DatabaseManager(ODailyQuests plugin) {
        this.plugin = plugin;
    }

    public void load() {
        switch (Database.getMode()) {
            case MYSQL -> {
                this.sqlManager = new MySQLManager();
                this.yamlManager = null;
            }
            case H2 -> {
                this.sqlManager = new H2Manager();
                this.yamlManager = null;
            }
            case YAML -> {
                this.yamlManager = new YamlManager();
                this.sqlManager = null;
            }
        }
    }

    public void close() {
        if (this.sqlManager != null) {
            this.sqlManager.close();
        }
    }

    public void loadQuestsForPlayer(String playerName) {
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        switch (Database.getMode()) {
            case YAML -> yamlManager.getLoadProgressionYAML().loadPlayerQuests(playerName, activeQuests);
            case MYSQL, H2 -> sqlManager.getLoadProgressionSQL().loadProgression(playerName, activeQuests);
            default ->
                    PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
        }
    }

    public void saveProgressionForPlayer(String playerName, PlayerQuests playerQuests) {
        switch (Database.getMode()) {
            case YAML ->
                    yamlManager.getSaveProgressionYAML().saveProgression(playerName, playerQuests, plugin.isServerStopping());
            case MYSQL, H2 ->
                    sqlManager.getSaveProgressionSQL().saveProgression(playerName, playerQuests, plugin.isServerStopping());
            default ->
                    PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
        }
    }
}
