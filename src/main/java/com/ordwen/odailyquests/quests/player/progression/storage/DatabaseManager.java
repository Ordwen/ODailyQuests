package com.ordwen.odailyquests.quests.player.progression.storage;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.quests.player.PlayerQuests;
import com.ordwen.odailyquests.quests.player.QuestsManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.sqlite.SQLiteManager;
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
        close();
        this.sqlManager = null;
        this.yamlManager = null;

        switch (Database.getMode()) {
            case MYSQL -> this.sqlManager = new MySQLManager();
            case SQLITE -> this.sqlManager = new SQLiteManager();
            case YAML -> this.yamlManager = new YamlManager(plugin.getFilesManager().getProgressionFile());
        }
    }

    public void close() {
        if (this.sqlManager != null) {
            this.sqlManager.close();
        }
    }

    public void loadQuestsForPlayer(String playerName) {
        loadQuestsForPlayer(playerName, true);
    }

    public void loadQuestsForPlayer(String playerName, boolean sendStatusMessage) {
        final Map<String, PlayerQuests> activeQuests = QuestsManager.getActiveQuests();
        switch (Database.getMode()) {
            case YAML -> yamlManager.getLoadProgressionYAML().loadPlayerQuests(playerName, activeQuests, sendStatusMessage);
            case MYSQL, SQLITE -> sqlManager.getLoadProgressionSQL().loadProgression(playerName, activeQuests, sendStatusMessage);
            default ->
                    PluginLogger.error("Impossible to load player quests : the selected storage mode is incorrect !");
        }
    }

    public void saveProgressionForPlayer(String playerName, String playerUuid, PlayerQuests playerQuests) {
        switch (Database.getMode()) {
            case YAML ->
                    yamlManager.getSaveProgressionYAML().saveProgression(playerName, playerUuid, playerQuests, plugin.isServerStopping());
            case MYSQL, SQLITE ->
                    sqlManager.getSaveProgressionSQL().saveProgression(playerName, playerUuid, playerQuests, plugin.isServerStopping());
            default ->
                    PluginLogger.error("Impossible to save player quests : the selected storage mode is incorrect !");
        }
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public YamlManager getYamlManager() {
        return yamlManager;
    }
}
