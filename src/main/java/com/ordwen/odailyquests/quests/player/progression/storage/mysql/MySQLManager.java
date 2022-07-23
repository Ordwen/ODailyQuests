package com.ordwen.odailyquests.quests.player.progression.storage.mysql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;
import com.ordwen.odailyquests.tools.PluginLogger;

public class MySQLManager {

    /* init variables */

    // database settings
    private String host;
    private String dbName;
    private String password;
    private String user;
    private String port;

    // instances
    private final LoadProgressionSQL loadProgressionSQL;
    private final SaveProgressionSQL saveProgressionSQL;
    private final ConfigurationFiles configurationFiles;
    private HikariDataSource hikariDataSource;

    /**
     * Constructor.
     * @param oDailyQuests main class instance.
     */
    public MySQLManager(ODailyQuests oDailyQuests) {
        this.configurationFiles = oDailyQuests.getConfigurationFiles();

        this.loadProgressionSQL = new LoadProgressionSQL(this);
        this.saveProgressionSQL = new SaveProgressionSQL(this);
    }

    /**
     * Load identifiers for database connection.
     */
    public void initCredentials() {

        ConfigurationSection sqlSection= configurationFiles.getConfigFile().getConfigurationSection("database");

        host = sqlSection.getString("host");
        dbName = sqlSection.getString("name");
        password = sqlSection.getString("password");
        user = sqlSection.getString("user");
        port = sqlSection.getString("port");
    }

    /**
     * Test database connection.
     * @throws SQLException SQL errors.
     */
    public void testConnection() throws SQLException {
        Connection con = getConnection();
        if (con.isValid(1)) {
            PluginLogger.info(ChatColor.BLUE + "Plugin successfully connected to database " + con.getCatalog() + ".");
            con.close();
        } else PluginLogger.info(ChatColor.DARK_RED + "IMPOSSIBLE TO CONNECT TO DATABASE");
    }

    /**
     * Connect to database.
     */
    public void initHikariCP(){

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(this.toUri());
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setMaxLifetime(300000L);
        hikariConfig.setLeakDetectionThreshold(10000L);
        hikariConfig.setConnectionTimeout(10000L);

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Init database.
     */
    public void setupDatabase() {
        initCredentials();
        initHikariCP();

        try {
            testConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setupTables();
    }

    /**
     * Verify if tables exists, if not create them.
     */
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
     * @param tableName name of the table to check.
     * @return true if table exists.
     * @throws SQLException SQL errors.
     */
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});

        return resultSet.next();
    }

    /**
     * Close database connection.
     */
    public void close(){
        this.hikariDataSource.close();
    }

    /**
     * Get database connection.
     * @return database Connection.
     */
    public Connection getConnection() {
        if(this.hikariDataSource != null && !this.hikariDataSource.isClosed()){
            try {
                return this.hikariDataSource.getConnection();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Setup JdbcUrl.
     * @return JdbcUrl.
     */
    private String toUri(){
        return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbName;
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
