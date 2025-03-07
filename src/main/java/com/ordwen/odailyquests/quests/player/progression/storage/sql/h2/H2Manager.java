package com.ordwen.odailyquests.quests.player.progression.storage.sql.h2;

import com.ordwen.odailyquests.quests.player.progression.storage.sql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SaveProgressionSQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class H2Manager extends SQLManager {


    public H2Manager() {
        super.loadProgressionSQL = new LoadProgressionSQL(this);
        super.saveProgressionSQL = new SaveProgressionSQL(this);

        setupDatabase();
    }

    /**
     * Init database.
     */
    public void setupDatabase() {
        initH2();

        testConnection();
        setupTables();
    }

    private void initH2() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:./plugins/ODailyQuests/database");
        config.setUsername("odq");
        config.setPassword("");
        config.setMaximumPoolSize(100);
        config.setMaxLifetime(300000L);
        config.setLeakDetectionThreshold(60000L);
        config.setConnectionTimeout(60000L);
        super.hikariDataSource = new HikariDataSource(config);
    }
}
