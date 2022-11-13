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
            if (!tableExists(connection, "OBJECTS")) {

                String str = "CREATE TEMPORARY TABLE OBJECTS ("
                        + "PID INT NOT NULL AUTO_INCREMENT,"
                        + "PLAYER VARCHAR(16) NOT NULL,"
                        + "ID VARCHAR(255) NOT NULL,"
                        + "PRIMARY KEY (ID)"
                        + ");";

                PreparedStatement preparedStatement = connection.prepareStatement(str);
                preparedStatement.execute();

                preparedStatement.close();
            } else PluginLogger.error("Table OBJECTS already exists. That's not normal. Please contact the developer.");

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

    /**
     * Add a block to database.
     * @param player player name.
     * @param id serialized block.
     */
    public void addObjectId(String player, String id) {
        final Connection connection = getConnection();
        try {
            String str = "INSERT INTO OBJECTS (PLAYER, ID) VALUES (?, ?);";

            PreparedStatement preparedStatement = connection.prepareStatement(str);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, id);
            preparedStatement.execute();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if a player already used an id.
     * @param player player to check.
     * @param id id to check.
     */
    public boolean checkIfContainsObject(String player, String id) {
        final Connection connection = getConnection();
        String str = "SELECT * FROM OBJECTS WHERE PLAYER = ? AND ID = ?;";

        return containsRequest(player, id, connection, str);
    }

    /**
     * Check if a player already used an id.
     * @param player player to check.
     * @param id serialized id to check.
     * @param connection connection to database.
     * @param str SQL request.
     * @return true if player already used entity or block.
     */
    private boolean containsRequest(String player, String id, Connection connection, String str) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(str);
            preparedStatement.setString(1, player);
            preparedStatement.setString(2, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            boolean contains = resultSet.next();

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return contains;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
