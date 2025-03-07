package com.ordwen.odailyquests.quests.player.progression.storage.sql.sqlite;

import com.ordwen.odailyquests.quests.player.progression.storage.sql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SaveProgressionSQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class SQLiteManager extends SQLManager {


    public SQLiteManager() {
        super.loadProgressionSQL = new LoadProgressionSQL(this);
        super.saveProgressionSQL = new SaveProgressionSQL(this);

        setupDatabase();
    }

    /**
     * Init database.
     */
    public void setupDatabase() {
        initSQLite();

        testConnection();
        setupTables();
    }

    private void initSQLite() {
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:./plugins/ODailyQuests/database.db");
        config.setMaximumPoolSize(100);
        config.setMaxLifetime(300000L);
        config.setLeakDetectionThreshold(60000L);
        config.setConnectionTimeout(60000L);
        super.hikariDataSource = new HikariDataSource(config);
    }
}
