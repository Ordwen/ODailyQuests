package com.ordwen.odailyquests.tools.autoupdater.database;

import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.quests.player.progression.storage.DatabaseManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.yaml.YamlManager;
import com.ordwen.odailyquests.tools.PluginLogger;

public class MigrationManager {
    private final DatabaseManager databaseManager;

    public MigrationManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void runMigrations() {
        switch (Database.getMode()) {
            case MYSQL, SQLITE -> {
                SQLManager sqlManager = databaseManager.getSqlManager();
                if (sqlManager != null) {
                    final SQLMigrationManager sqlMigrationManager = new SQLMigrationManager(sqlManager);
                    sqlMigrationManager.runMigrations();
                } else {
                    PluginLogger.error("SQLManager non disponible pour les migrations SQL.");
                }
            }
            case YAML -> {
                YamlManager yamlManager = databaseManager.getYamlManager();
                if (yamlManager != null) {
                    final YamlMigrationManager yamlMigrationManager = new YamlMigrationManager(yamlManager);
                    yamlMigrationManager.runMigrations();
                } else {
                    PluginLogger.error("YamlManager non disponible pour les migrations YAML.");
                }
            }
            default -> PluginLogger.error("Le mode de stockage n'est pas supporté pour les migrations.");
        }
    }
}
