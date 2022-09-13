package com.ordwen.odailyquests.quests.player.progression.storage.sql.h2;

import com.ordwen.odailyquests.ODailyQuests;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.LoadProgressionSQL;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;
import com.ordwen.odailyquests.quests.player.progression.storage.sql.SaveProgressionSQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class H2Manager extends SQLManager {

    private final ODailyQuests oDailyQuests;

    public H2Manager(ODailyQuests oDailyQuests) {
        this.oDailyQuests = oDailyQuests;

        super.loadProgressionSQL = new LoadProgressionSQL(this);
        super.saveProgressionSQL = new SaveProgressionSQL(this);

        setupDatabase();
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

    private  void initH2() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:file:" + oDailyQuests.getDataFolder() + "/database");
        config.setUsername("odq");
        config.setPassword("");
        config.setMaxLifetime(300000L);
        config.setLeakDetectionThreshold(10000L);
        config.setConnectionTimeout(10000L);

        super.hikariDataSource = new HikariDataSource(config);
    }
}
