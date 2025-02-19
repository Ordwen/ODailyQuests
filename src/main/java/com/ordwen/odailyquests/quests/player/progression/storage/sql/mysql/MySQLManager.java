package com.ordwen.odailyquests.quests.player.progression.storage.sql.mysql;

import com.ordwen.odailyquests.configuration.essentials.Database;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SaveProgressionSQL;
import com.ordwen.odailyquests.tools.PluginLogger;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class MySQLManager extends SQLManager {

    public MySQLManager() {
        super.loadProgressionSQL = new LoadProgressionSQL(this);
        super.saveProgressionSQL = new SaveProgressionSQL(this);

        setupDatabase();
    }

    /**
     * Connect to database.
     */
    public void initHikariCP(){
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setJdbcUrl(this.toUri());
        hikariConfig.setUsername(Database.getUser());
        hikariConfig.setPassword(Database.getPassword());
        hikariConfig.setMaxLifetime(300000L);
        hikariConfig.setLeakDetectionThreshold(10000L);
        hikariConfig.setConnectionTimeout(10000L);

        super.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    /**
     * Init database.
     */
    public void setupDatabase() {
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
        return "jdbc:mysql://" + Database.getHost() + ":" + Database.getPort() + "/" + Database.getName();
    }

}
