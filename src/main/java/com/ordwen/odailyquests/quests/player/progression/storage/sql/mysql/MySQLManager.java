package com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.files.ConfigurationFiles;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SaveProgressionSQL;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.*;

public class MySQLManager extends SQLManager {

    /* init variables */

    // database settings
    private String host;
    private String dbName;
    private String password;
    private String user;
    private String port;

    // instances
    private final ConfigurationFiles configurationFiles;

    /**
     * Constructor.
     * @param oDailyQuests main class instance.
     */
    public MySQLManager(ODailyQuests oDailyQuests) {
        this.configurationFiles = oDailyQuests.getConfigurationFiles();

        super.loadProgressionSQL = new LoadProgressionSQL(this);
        super.saveProgressionSQL = new SaveProgressionSQL(this);

        setupDatabase();
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

        super.hikariDataSource = new HikariDataSource(hikariConfig);
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
            PluginLogger.error(e.getMessage());
        }

        setupTables();
    }

    /**
     * Setup JdbcUrl.
     * @return JdbcUrl.
     */
    private String toUri(){
        return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbName;
    }

}
