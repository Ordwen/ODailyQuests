package com.ordwen.odailyquests.tools.updater.database;

import com.ordwen.odailyquests.quests.player.progression.storage.sql.SQLManager;

public class SQLMigrationManager {

    private final SQLManager sqlManager;

    public SQLMigrationManager(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public void runMigrations() {

    }
}
