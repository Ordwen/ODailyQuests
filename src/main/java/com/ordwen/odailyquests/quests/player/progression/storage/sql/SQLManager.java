package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.configuration.essentials.Debugger;
import com.ordwen.odailyquests.enums.SQLQuery;
import com.ordwen.odailyquests.enums.StorageMode;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public abstract class SQLManager {

    protected HikariDataSource hikariDataSource;

    protected LoadProgressionSQL loadProgressionSQL;
    protected SaveProgressionSQL saveProgressionSQL;

    public void setupTables() {
        try (final Connection connection = getConnection();
             final PreparedStatement playerStatement = connection.prepareStatement(Database.getMode() == StorageMode.MYSQL ? SQLQuery.MYSQL_CREATE_PLAYER_TABLE.getQuery() : SQLQuery.SQLITE_CREATE_PLAYER_TABLE.getQuery());
             final PreparedStatement progressionStatement = connection.prepareStatement(Database.getMode() == StorageMode.MYSQL ? SQLQuery.MYSQL_CREATE_PROGRESSION_TABLE.getQuery() : SQLQuery.SQLITE_CREATE_PROGRESSION_TABLE.getQuery());
             final PreparedStatement categoryStatsStatement = connection.prepareStatement(Database.getMode() == StorageMode.MYSQL ? SQLQuery.MYSQL_CREATE_PLAYER_CATEGORY_STATS_TABLE.getQuery() : SQLQuery.SQLITE_CREATE_PLAYER_CATEGORY_STATS_TABLE.getQuery())) {

            playerStatement.execute();
            Debugger.write("Table odq_player created or found in database.");

            progressionStatement.execute();
            Debugger.write("Table odq_progression created or found in database.");

            categoryStatsStatement.execute();
            Debugger.write("Table odq_player_category_stats created or found in database.");

        } catch (SQLException e) {
            PluginLogger.error(e.getMessage());
        }
    }

    /**
     * Close database connection.
     */
    public void close() {
        if (this.hikariDataSource != null && !this.hikariDataSource.isClosed()) {
            this.hikariDataSource.close();
        }
    }

    /**
     * Get database connection.
     *
     * @return database Connection.
     */
    public Connection getConnection() {
        if (this.hikariDataSource != null && !this.hikariDataSource.isClosed()) {
            try {
                return this.hikariDataSource.getConnection();
            } catch (SQLException e) {
                PluginLogger.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * Test database connection.
     */
    protected void testConnection() {
        try (Connection con = getConnection()) {
            if (con != null && con.isValid(1)) {
                PluginLogger.info("Plugin successfully connected to database.");
            } else {
                PluginLogger.error("Impossible to connect to database, please check your configuration.");
            }
        } catch (SQLException e) {
            PluginLogger.error("Impossible to connect to database.");
            PluginLogger.error(e.getMessage());
        }
    }

    /**
     * Get load progression SQL instance.
     *
     * @return load progression SQL instance.
     */
    public LoadProgressionSQL getLoadProgressionSQL() {
        return loadProgressionSQL;
    }

    /**
     * Get save progression SQL instance.
     *
     * @return save progression SQL instance.
     */
    public SaveProgressionSQL getSaveProgressionSQL() {
        return saveProgressionSQL;
    }
}
