package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public abstract class SQLManager {

    protected HikariDataSource hikariDataSource;

    protected LoadProgressionSQL loadProgressionSQL;
    protected SaveProgressionSQL saveProgressionSQL;

    public void setupTables() {
        final Connection connection = getConnection();
        try {
            ODailyQuests.questSystemMap.forEach((key, questSystem) -> {
                createTable(questSystem.getPlayerTableName(), questSystem.getPlayerTableSQL(), connection);
                createTable(questSystem.getProgressionTableName(), questSystem.getProgressionTableSQL(), connection);
            });
            connection.close();
        } catch (SQLException e) {
            PluginLogger.error(e.getMessage());
        }
    }

    public void createTable(String tableName, String tableCreation, Connection connection) {
        try {
            if (!tableExists(connection, tableName)) {

                PreparedStatement preparedStatement = connection.prepareStatement(tableCreation);
                preparedStatement.execute();

                preparedStatement.close();
                PluginLogger.info("Table '" + tableName + "' created in database.");
            }
        } catch (SQLException e) {
            PluginLogger.error(e.getMessage());
        }
    }

    /**
     * Check if a table exists in database.
     *
     * @param connection connection to check.
     * @param tableName  name of the table to check.
     * @return true if table exists.
     * @throws SQLException SQL errors.
     */
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

        return resultSet.next();
    }

    /**
     * Close database connection.
     */
    public void close() {
        this.hikariDataSource.close();
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
     *
     * @throws SQLException SQL errors.
     */
    protected void testConnection() throws SQLException {
        Connection con = getConnection();
        if (con.isValid(1)) {
            PluginLogger.info("Plugin successfully connected to database " + con.getCatalog() + ".");
            con.close();
        } else PluginLogger.error("IMPOSSIBLE TO CONNECT TO DATABASE");
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
