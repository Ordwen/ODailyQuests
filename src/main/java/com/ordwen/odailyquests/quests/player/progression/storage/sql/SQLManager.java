package com.ordwen.odailyquests.quests.player.progression.storage.sql;

import com.ordwen.odailyquests.tools.PluginLogger;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.ChatColor;

import java.sql.*;

public abstract class SQLManager {

    protected HikariDataSource hikariDataSource;

    protected LoadProgressionSQL loadProgressionSQL;
    protected SaveProgressionSQL saveProgressionSQL;

    public void setupTables() {
        Connection connection = getConnection();
        try {
            if (!tableExists(connection, "PLAYER")) {

                String str = "create table PLAYER\n" +
                        "  (\n" +
                        "     PLAYERNAME char(32)  not null  ,\n" +
                        "     PLAYERTIMESTAMP bigint not null,  \n" +
                        "     ACHIEVEDQUESTS tinyint not null, \n" +
                        "     TOTALACHIEVEDQUESTS int not null, \n" +
                        "     constraint PK_PLAYER primary key (PLAYERNAME)\n" +
                        "  );";

                PreparedStatement preparedStatement = connection.prepareStatement(str);
                preparedStatement.execute();

                preparedStatement.close();
                PluginLogger.info(ChatColor.BLUE + "Table 'Player' created in database.");
            }
            if (!tableExists(connection, "PROGRESSION")) {

                String str = "create table PROGRESSION\n" +
                        "  (\n" +
                        "     PRIMARYKEY smallint auto_increment  ,\n" +
                        "     PLAYERNAME char(32)  not null  ,\n" +
                        "     PLAYERQUESTID tinyint  not null  ,\n" +
                        "     QUESTINDEX int  not null  ,\n" +
                        "     ADVANCEMENT int  not null  ,\n" +
                        "     ISACHIEVED bit  not null  \n" +
                        "     ,\n" +
                        "     primary key (PRIMARYKEY)\n" +
                        "  ); ";

                PreparedStatement preparedStatement = connection.prepareStatement(str);
                preparedStatement.execute();

                preparedStatement.close();
                PluginLogger.info(ChatColor.BLUE + "Table 'Progression' created in database.");
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
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return null;
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
     * Get load progression SQL instance.
     * @return load progression SQL instance.
     */
    public LoadProgressionSQL getLoadProgressionSQL() {
        return loadProgressionSQL;
    }

    /**
     * Get save progression SQL instance.
     * @return save progression SQL instance.
     */
    public SaveProgressionSQL getSaveProgressionSQL() {
        return saveProgressionSQL;
    }
}
