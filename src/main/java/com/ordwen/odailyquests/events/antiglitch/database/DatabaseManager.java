package com.ordwen.odailyquests.events.antiglitch.database;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.ChatColor;

import java.sql.*;

public class DatabaseManager {

    private final ODailyQuests oDailyQuests;

    private HikariDataSource hikariDataSource;

    public DatabaseManager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;
    }

    /**
     * Init database.
     */
    public void setupDatabase() {
        initH2();

        try {
            testConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setupTables();
    }

    /**
     * Test database connection.
     * @throws SQLException SQL errors.
     */
    protected void testConnection() throws SQLException {
        Connection con = getConnection();
        if (con.isValid(1)) {
            PluginLogger.info(ChatColor.BLUE + "Plugin successfully connected to database " + con.getCatalog() + ".");
            con.close();
        } else PluginLogger.info(ChatColor.DARK_RED + "IMPOSSIBLE TO CONNECT TO DATABASE");
    }

    /**
     * Close database connection.
     */
    public void close() {
        this.hikariDataSource.close();
    }

    public void setupTables() {
        final Connection connection = getConnection();
        try {
            if (!tableExists(connection, "BLOCKS")) {

                String str = "";

                PreparedStatement preparedStatement = connection.prepareStatement(str);
                preparedStatement.execute();

                preparedStatement.close();
            }
            if (!tableExists(connection, "BLOCKS")) {

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void initH2() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:./plugins/ODailyQuests/antiglitch");
        config.setUsername("odq");
        config.setPassword("");
        config.setMaxLifetime(300000L);
        config.setLeakDetectionThreshold(10000L);
        config.setConnectionTimeout(10000L);

        this.hikariDataSource = new HikariDataSource(config);
    }

    /**
     * Get database connection.
     * @return database Connection.
     */
    public Connection getConnection() {
        if (this.hikariDataSource != null && !this.hikariDataSource.isClosed()) {
            try {
                return this.hikariDataSource.getConnection();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return null;
    }
}
